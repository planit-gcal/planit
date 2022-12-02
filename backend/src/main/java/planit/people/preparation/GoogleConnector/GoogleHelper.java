package planit.people.preparation.GoogleConnector;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.common.util.concurrent.*;
import org.joda.time.Duration;
import org.joda.time.Interval;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Entities.Entity_GoogleAccount;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Scheduling.Converter;
import planit.people.preparation.Scheduling.Scheduler;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

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

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail, Map<Long, Set<String>> userGoogleAccountMappedToPlanItUserId) throws IOException, ExecutionException, InterruptedException, ParseException {
        if (testMethod()){
            return null;
        }
        //DateTime startDate = getStartDate(newEventDetail.start_date(), newEventDetail.end_date(), newEventDetail.duration(), userGoogleAccountMappedToPlanItUserId);
        //TODO return this when function is ready. return googleConnector.createEvent(newEventDetail, startDate);
        return new CalendarResponse(null, null);
    }

    public DateTime getStartDate(Date startDate, Date endDate, Long duration, Map<Long, Set<String>> userGoogleAccountMappedToPlanItUserId) throws IOException, ExecutionException, InterruptedException {
        org.joda.time.DateTime jodaStartDateTime = new org.joda.time.DateTime(startDate);
        org.joda.time.DateTime jodaEndDateTime = new org.joda.time.DateTime(endDate);
        Duration durationInMinutes = Duration.standardMinutes(duration);
        Map<Long, List<Interval>> freeTimeForAll = new HashMap<>();
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ListeningExecutorService service = MoreExecutors.listeningDecorator(threadPool);
        for (Long userId : userGoogleAccountMappedToPlanItUserId.keySet()) {

           Future<List<Interval>> futureTask = threadPool.submit(() ->getFreeBusyForAllUserAccounts(
                    startDate,
                    endDate,
                    userGoogleAccountMappedToPlanItUserId.get(userId)));
            freeTimeForAll.put(userId,
                    Scheduler.getAllAvailable(
                            futureTask.get(),
                            jodaStartDateTime,
                            jodaEndDateTime));
        }
        System.out.println("freeTimeForAll: " + freeTimeForAll);
        //TODO call the search method.
        return new DateTime(startDate);
    }

    private List<Interval> getFreeBusyForAllUserAccounts(Date startDate, Date endDate, Set<String> refreshTokens) throws IOException {
        List<Interval> busyForAll = new ArrayList<>();
        for (String refreshToken : refreshTokens) {
            busyForAll.addAll(getFreeBusy(startDate, endDate, refreshToken));
        }
        return busyForAll;
    }

        public Boolean testMethod() throws ParseException, IOException, ExecutionException, InterruptedException {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        ListeningExecutorService service = MoreExecutors.listeningDecorator(threadPool);
        String rt = "1//0czdxbvk7c4shCgYIARAAGAwSNwF-L9IrrtSj9JbSg27ARcBp1pki4nBnIOpTXsRlnSyECFpCJcy4523v-ATkUCczyso-Y5N2OV0";
        GoogleConnector googleConnectorForIndividual = new GoogleConnector(rt);

        Date sDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2022-11-20 10:00:00");
        Date eDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2023-01-20 10:00:00");
        for (int i = 0; i < 100; i++) {
            Future<FreeBusyResponse> responseFuture = threadPool.submit(() ->googleConnectorForIndividual.getFreeBusy(sDate, eDate));
            if(responseFuture.isDone()) {
                System.out.println("isDone");
                return true;
            }
        }
        return false;
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


}
