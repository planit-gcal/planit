package planit.people.preparation.GoogleConnector;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.FreeBusyResponse;
import com.google.api.services.calendar.model.TimePeriod;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import org.joda.time.Duration;
import org.joda.time.Interval;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Scheduling.Converter;
import planit.people.preparation.Scheduling.Scheduler;

import java.io.IOException;
import java.util.Date;
import java.util.Set;
import java.util.Vector;

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

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail, Set<String> refreshTokens) throws IOException {
        DateTime startDate = getStartDate(newEventDetail.start_date(), newEventDetail.end_date(), newEventDetail.duration(), refreshTokens);
        return googleConnector.createEvent(newEventDetail, startDate);
    }

    public DateTime getStartDate(Date startDate, Date endDate, Long duration, Set<String> refreshTokens) throws IOException {
        org.joda.time.DateTime jodaStartDateTime = new org.joda.time.DateTime(startDate);
        org.joda.time.DateTime jodaStopDateTime = new org.joda.time.DateTime(endDate);
        Duration durationInMinutes = Duration.standardMinutes(duration);
        Interval firstInterval = Scheduler.getOneTimeSlotBetweenDatesOfLength(getFreeBusyForAll(startDate, endDate, refreshTokens), durationInMinutes, jodaStartDateTime, jodaStopDateTime);
        Date eventStartDate = firstInterval.getStart().toDate();
        return new DateTime(eventStartDate);
    }

    private Vector<Interval> getFreeBusyForAll(Date startDate, Date endDate, Set<String> refreshTokens) throws IOException {
        Vector<Interval> busyForAll = new Vector<>();
        for (String refreshToken : refreshTokens) {
            busyForAll.addAll(getFreeBusy(startDate, endDate, refreshToken));
        }
        return busyForAll;
    }

    public Vector<Interval> getFreeBusy(Date startDate, Date endDate, String refreshToken) throws IOException {
        GoogleConnector googleConnectorForIndividual = new GoogleConnector(refreshToken);
        return getBusyIntervals(googleConnectorForIndividual.getFreeBusy(startDate, endDate));
    }

    private Vector<Interval> getBusyIntervals(FreeBusyResponse freeBusyResponse) {
        Vector<TimePeriod> busyTimePeriods = new Vector<>();
        for (String calendarId : freeBusyResponse.getCalendars().keySet()) {
            busyTimePeriods.addAll(freeBusyResponse.getCalendars().get(calendarId).getBusy());
        }
        return Converter.covertTimePeriodsToIntervals(busyTimePeriods);
    }


}
