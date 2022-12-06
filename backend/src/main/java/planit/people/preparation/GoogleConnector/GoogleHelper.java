package planit.people.preparation.GoogleConnector;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import org.joda.time.Duration;
import org.joda.time.Interval;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Entities.Entity_PresetAvailability;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Scheduling.Converter;
import planit.people.preparation.Scheduling.FreeTimeFinder;
import planit.people.preparation.Scheduling.Scheduler;
import planit.people.preparation.Scheduling.SchedulingInfo;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class GoogleHelper {
    private final GoogleConnector googleConnector = new GoogleConnector();

    public GoogleHelper(String code, Boolean refreshToken) {
        if (refreshToken) {
            googleConnector.setRefreshToken(code);
        } else {
            googleConnector.setCode(code);
        }
    }

    public Userinfo getUserInfo() throws IOException {
        Oauth2 oauth2 = googleConnector.oauth2Service();
        System.out.println("oauth2: " + oauth2.userinfo().get().execute());
        return oauth2.userinfo().get().execute();
    }


    public String getRefreshToken() {
        return googleConnector.getRefreshToken();
    }

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail, Map<String, Map<Long, Set<String>>> refreshTokenForAllGuests) throws IOException, ExecutionException, InterruptedException {
        DateTime startDate = getStartDate(newEventDetail, refreshTokenForAllGuests);
        System.out.println("StartDate: " + startDate);
        googleConnector.createEvent(newEventDetail, startDate);
        return new CalendarResponse(startDate, startDate);
    }

    public DateTime getStartDate(DTO_NewEventDetail newEventDetail, Map<String, Map<Long, Set<String>>> refreshTokenForAllGuests) throws ExecutionException, InterruptedException {
        Duration durationInMinutes = Duration.standardMinutes(newEventDetail.duration());
        Map<String, Map<Long, List<Interval>>> freeBusyIntervalsForAllUsers = getFreeBusyIntervalForAll(newEventDetail.start_date(), newEventDetail.end_date(), refreshTokenForAllGuests);
        Map<String, Map<Long, List<Interval>>> freeIntervalsForAllUsers = getFreeIntervalForAll(freeBusyIntervalsForAllUsers, newEventDetail.start_date(), newEventDetail.end_date(), durationInMinutes, newEventDetail.event_preset_detail().preset_availability());
        List<SchedulingInfo> schedulingInfos = Converter.convertIntervalMapToListOfSchedulingInfo(freeIntervalsForAllUsers);
        Date startTime = FreeTimeFinder.getBestStartDate(schedulingInfos, durationInMinutes);
        assert startTime != null;
        return new DateTime(startTime);
    }

    private Map<String, Map<Long, List<Interval>>> getFreeBusyIntervalForAll(Date startDate, Date endDate, Map<String, Map<Long, Set<String>>> usersRefreshToken) throws ExecutionException, InterruptedException {
        Map<String, Map<Long, List<CompletableFuture<List<Interval>>>>> futureCallsMap = new HashMap<>();
        List<CompletableFuture<List<Interval>>> futureCalls = new ArrayList<>();
        int size = -1;
        for (String obligation : usersRefreshToken.keySet()) {
            futureCallsMap.put(obligation, new HashMap<>());
            for (Long userId : usersRefreshToken.get(obligation).keySet()) {
                futureCallsMap.get(obligation).put(userId, new ArrayList<>());
                List<CompletableFuture<List<Interval>>> futureIntervalForAUser = new ArrayList<>();
                for (String refreshToken : usersRefreshToken.get(obligation).get(userId).stream().toList()) {
                    size += 1;
                    CompletableFuture<List<Interval>> futureCall = CompletableFuture.supplyAsync(() -> {
                        try {
                            return getFreeBusy(startDate, endDate, refreshToken);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    futureCalls.add(futureCall);
                    futureIntervalForAUser.add(futureCall);

                }
                futureCallsMap.get(obligation).get(userId).addAll(futureIntervalForAUser);
            }
        }
        CompletableFuture<Void> combinedCalls = CompletableFuture.allOf(futureCalls.toArray(new CompletableFuture[size]));
        combinedCalls.get();
        return Converter.convertFutureMapToIntervalMap(futureCallsMap);
    }

    public List<Interval> getFreeBusy(Date startDate, Date endDate, String refreshToken) throws IOException {
        GoogleConnector googleConnectorForIndividual = new GoogleConnector(refreshToken);
        return getBusyIntervals(googleConnectorForIndividual.getFreeBusy(startDate, endDate));
    }

    private List<Interval> getBusyIntervals(FreeBusyResponse freeBusyResponse) {
        List<TimePeriod> busyTimePeriods = new ArrayList<>();
        for (String calendarId : freeBusyResponse.getCalendars().keySet()) {
            busyTimePeriods.addAll(freeBusyResponse.getCalendars().get(calendarId).getBusy());
        }
        return Converter.covertTimePeriodsToIntervals(busyTimePeriods);
    }

    private Map<String, Map<Long, List<Interval>>> getFreeIntervalForAll(Map<String, Map<Long, List<Interval>>> freeBusyForAll, Date startTime, Date endTime, Duration duration, List<Entity_PresetAvailability> presetAvailabilities) throws ExecutionException, InterruptedException {
        Map<String, Map<Long, CompletableFuture<List<Interval>>>> freeIntervalsForAllUsers = new HashMap<>();
        Map<String, Map<Long, List<Interval>>> result = new HashMap<>();
        List<CompletableFuture<List<Interval>>> futureCalls = new ArrayList<>();
        int size = -1;
        for (String key : freeBusyForAll.keySet()) {
            freeIntervalsForAllUsers.put(key, new HashMap<>());
            for (Long id : freeBusyForAll.get(key).keySet()) {
                size += 1;
                CompletableFuture<List<Interval>> futureCall = CompletableFuture.supplyAsync(() -> getFreeIntervalsForAUser(freeBusyForAll.get(key).get(id), startTime, endTime, duration, presetAvailabilities));
                freeIntervalsForAllUsers.get(key).put(id, futureCall);
                futureCalls.add(futureCall);
            }
        }
        CompletableFuture<Void> combinedCalls = CompletableFuture.allOf(futureCalls.toArray(new CompletableFuture[size]));
        combinedCalls.get();

        for (String key : freeIntervalsForAllUsers.keySet()) {
            result.put(key, new HashMap<>());
            for (Long id : freeIntervalsForAllUsers.get(key).keySet()) {
                result.get(key).put(id, freeIntervalsForAllUsers.get(key).get(id).get());
            }
        }
        return result;
    }

    private List<Interval> getFreeIntervalsForAUser(List<Interval> freeBusyForUser, Date startDate, Date endDate, Duration duration, List<Entity_PresetAvailability> presetAvailabilities) {
        org.joda.time.DateTime jodaStartDateTime = new org.joda.time.DateTime(startDate);
        org.joda.time.DateTime jodaEndDateTime = new org.joda.time.DateTime(endDate);
        return Scheduler.getAvailableTimeSlots(freeBusyForUser, duration, jodaStartDateTime, jodaEndDateTime, presetAvailabilities);
    }


}
