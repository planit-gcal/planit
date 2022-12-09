package planit.people.preparation.GoogleConnector;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import org.jetbrains.annotations.Nullable;
import org.joda.time.Duration;
import org.joda.time.Interval;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Entities.Entity_PresetAvailability;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Scheduling.Converter;
import planit.people.preparation.Scheduling.BestDateFinder;
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

    /**
     * Create a new event in google calendar. 
     * 
     * @param newEventDetail  event detail provided in the request. 
     * @param refreshTokenForAllGuests a map containing the obligation to attend for all registered guests and the refresh token for all of their google accounts. The map structure is the following: Map<Obligation to attend, Map<PlanIt User Id, Set<Refresh Token>>> 
     * @see GoogleHelper#getStartDate(DTO_NewEventDetail, Map<String, Map<Long, Set<String>>>)
     * @see GoogleConnector#createEvent(DTO_NewEventDetail, DateTime)
     */
    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail, Map<String, Map<Long, Set<String>>> refreshTokenForAllGuests) throws IOException, ExecutionException, InterruptedException {
        DateTime startDate = getStartDate(newEventDetail, refreshTokenForAllGuests);
        if(startDate == null)
        {
            System.out.println("No date could be found");
            return new CalendarResponse(new DateTime(0), new DateTime(0));
        }
        System.out.println("StartDate: " + startDate);
        googleConnector.createEvent(newEventDetail, startDate);
        return new CalendarResponse(startDate, startDate);
    }

    /**
     * Get event start date. the method will perform the following steps: 
     * 1 - Get FreeBusy from Google for all registered and invited guests. 
     * 2 - Using the Free Busy in step 1 get all Free Interval for all invited and registered guests, taken into account the preset availability. 
     * 3 - Convert the free intervals from step 2 into SchedulingInfo objects. 
     * 4 - Run the algorithm to find the best time slot from the provided SchedulingInfo records. 
     * 
     * @param newEventDetail new event details
     * @param refreshTokenForAllGuests a map containing the obligation to attend for all registered guests and the refresh token for all of their google accounts. The map structure is the following: Map<Obligation to attend, Map<PlanIt User Id, Set<Refresh Token>>> 
     * @return DateTime the start datetime of the invite. 
     * @see GoogleHelper#getFreeBusyIntervalForAll(Date, Date, Map<String, Map<Long, Set<String>>>)
     * @see GoogleHelper#getFreeIntervalForAll(Map<String, Map<Long, List<Interval>>>, Date, Date, Duration, List<Entity_PresetAvailability>)
     * @see Converter#convertIntervalMapToListOfSchedulingInfo(Map<String, Map<Long, List<Interval>>>)
     * @see BestDateFinder#getBestStartDate(List<SchedulingInfo>, Duration)
     */
    public @Nullable DateTime getStartDate(DTO_NewEventDetail newEventDetail, Map<String, Map<Long, Set<String>>> refreshTokenForAllGuests) throws ExecutionException, InterruptedException {
        Duration durationInMinutes = Duration.standardMinutes(newEventDetail.duration());
        Map<String, Map<Long, List<Interval>>> freeBusyIntervalsForAllUsers = getFreeBusyIntervalForAll(newEventDetail.start_date(), newEventDetail.end_date(), refreshTokenForAllGuests);
        Map<String, Map<Long, List<Interval>>> freeIntervalsForAllUsers = getFreeIntervalForAll(freeBusyIntervalsForAllUsers, newEventDetail.start_date(), newEventDetail.end_date(), durationInMinutes, newEventDetail.event_preset_detail().preset_availability());
        List<SchedulingInfo> schedulingInfos = Converter.convertIntervalMapToListOfSchedulingInfo(freeIntervalsForAllUsers);
        Date startTime = BestDateFinder.getBestStartDate(schedulingInfos, durationInMinutes);
        if(startTime == null)
        {
            return null;
        }
        return new DateTime(startTime);
    }

    /**
     * Get free busy for all registered guests asynchronously using CompletableFuture. 
     * The method iterate through all refresh token for each guest and get all FreeBusy responses for each Refresh Token asynchronously.
     * 
     * @param startDate the date after which an event should be scheduled 
     * @param endDate the date before which an event should be scheduled. 
     * @param usersRefreshToken a map containing the obligation to attend for all registered guests and the refresh token for all of their google accounts. The map structure is the following: Map<Obligation to attend, Map<PlanIt User Id, Set<Refresh Token>>>
     * @return Map<String, Map<Long, List<Interval>>> a map containing all FreeBusy intervals mapped to their PlanIt User Id, which mapped to the obligation of attending the event. Map<Obligation to attend, Map<PlanIt User Id, List<FreeBusy Intervals>>>
     * @see GoogleHelper#getFreeBusy
     * @see Converter#convertFutureMapToIntervalMap
     * @see CompletableFuture
     */
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

    /**
     * Get all FreeBusy intervals for a google account.
     * 
     * @param startDate the date after which an event should be scheduled 
     * @param endDate the date before which an event should be scheduled. 
     * @param refreshToken google account refresh token that needs to be used in order to retrieve the FreeBusy from Google. 
     * @return List<Interval>  list of FreeBusy intervals. 
     * @see GoogleHelper#getBusyIntervals(FreeBusyResponse)
     * @see GoogleConnector#getFreeBusy(Date, Date)
     */
    public List<Interval> getFreeBusy(Date startDate, Date endDate, String refreshToken) throws IOException {
        GoogleConnector googleConnectorForIndividual = new GoogleConnector(refreshToken);
        return getBusyIntervals(googleConnectorForIndividual.getFreeBusy(startDate, endDate));
    }

    /**
     * Get all FreeBusy intervals from the FreeBusyResponse body. 
     * 
     * @param freeBusyResponse FreeBusy response object which will be used to extract the busy intervals. 
     * @return List<Interval>  list of FreeBusy intervals.
     * @see Converter#covertTimePeriodsToIntervals(List<TimePeriod>)
     */
    private List<Interval> getBusyIntervals(FreeBusyResponse freeBusyResponse) {
        List<TimePeriod> busyTimePeriods = new ArrayList<>();
        for (String calendarId : freeBusyResponse.getCalendars().keySet()) {
            busyTimePeriods.addAll(freeBusyResponse.getCalendars().get(calendarId).getBusy());
        }
        return Converter.covertTimePeriodsToIntervals(busyTimePeriods);
    }

    /**
     * Get Free Intervals for all users from the FreeBusy Intervals and event details asynchronously using CompletableFuture. 
     * The method iterate through all lists of FreeBusy intervals for each user and gets the available time slots for each user. 
     * 
     * @param freeBusyForAll a map containing all FreeBusy intervals mapped to their PlanIt User Id, which mapped to the obligation of attending the event. Map<Obligation to attend, Map<PlanIt User Id, List<FreeBusy Intervals>>> 
     * @param startDate the date after which an event should be scheduled 
     * @param endDate the date before which an event should be scheduled. 
     * @param duration the duration of the event 
     * @param presetAvailabilities list of the event preset availability. 
     * @return Map<String, Map<Long, List<Interval>>> a map containing all free intervals mapped to their PlanIt User Id, which is mapped to the obligation to attend the event. Map<Obligation to attend, Map<PlanIt User Id, List<Free Time Slots Intervals>>> 
     * @see GoogleHelper#getFreeIntervalsForAUser(List<Interval>, Date, Date, Duration, List<Entity_PresetAvailability>)
     */
    private Map<String, Map<Long, List<Interval>>> getFreeIntervalForAll(Map<String, Map<Long, List<Interval>>> freeBusyForAll, Date startDate, Date endDate, Duration duration, List<Entity_PresetAvailability> presetAvailabilities) throws ExecutionException, InterruptedException {
        Map<String, Map<Long, CompletableFuture<List<Interval>>>> freeIntervalsForAllUsers = new HashMap<>();
        Map<String, Map<Long, List<Interval>>> result = new HashMap<>();
        List<CompletableFuture<List<Interval>>> futureCalls = new ArrayList<>();
        int size = -1;
        for (String key : freeBusyForAll.keySet()) {
            freeIntervalsForAllUsers.put(key, new HashMap<>());
            for (Long id : freeBusyForAll.get(key).keySet()) {
                size += 1;
                CompletableFuture<List<Interval>> futureCall = CompletableFuture.supplyAsync(() -> getFreeIntervalsForAUser(freeBusyForAll.get(key).get(id), startDate, endDate, duration, presetAvailabilities));
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

    /**
     * Get free time slots for a user using their free busy intervals and event details. 
     * 
     * @param freeBusyForUser a list of FreeBusy intervals for user. 
     * @param startDate the date after which an event should be scheduled 
     * @param endDate the date before which an event should be scheduled. 
     * @param duration the duration of the event 
     * @param presetAvailabilities list of the event preset availability. 
     * @return List<Interval> a list of Free time slots for the user. 
     * @see Scheduler#getAvailableTimeSlots(List<Interval>, Duration, org.joda.time.DateTime, org.joda.time.DateTime, List<Entity_PresetAvailability>)
     */
    private List<Interval> getFreeIntervalsForAUser(List<Interval> freeBusyForUser, Date startDate, Date endDate, Duration duration, List<Entity_PresetAvailability> presetAvailabilities) {
        org.joda.time.DateTime jodaStartDateTime = new org.joda.time.DateTime(startDate);
        org.joda.time.DateTime jodaEndDateTime = new org.joda.time.DateTime(endDate);
        return Scheduler.getAvailableTimeSlots(freeBusyForUser, duration, jodaStartDateTime, jodaEndDateTime, presetAvailabilities);
    }


}
