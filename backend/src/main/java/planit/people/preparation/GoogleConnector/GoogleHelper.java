package planit.people.preparation.GoogleConnector;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import org.joda.time.Duration;
import org.joda.time.Interval;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Scheduling.Converter;
import planit.people.preparation.Scheduling.Scheduler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.Vector;

public class GoogleHelper {
    private static final String TIME_ZONE_SPECIFIER = "UTC";
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

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail) throws IOException {
        Event event = new Event()
                .setSummary(newEventDetail.summary())
                .setLocation(newEventDetail.location())
                .setDescription(newEventDetail.description());
        DateTime startDate = getStartDate(newEventDetail.startDate(), newEventDetail.endDate(), newEventDetail.duration());
        System.out.println("Start Date: " + startDate);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                //TODO: STORE TIME ZONE
                .setTimeZone(TIME_ZONE_SPECIFIER);
        event.setStart(start);

        DateTime endDateTime = new DateTime(startDate.getValue() + (newEventDetail.duration() * 60 * 1000));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(TIME_ZONE_SPECIFIER);
        event.setEnd(end);

        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=1"};
        event.setRecurrence(Arrays.asList(recurrence));

        List<EventAttendee> attendees = new ArrayList<>();
        for (String attendee : newEventDetail.attendees()) {
            attendees.add(new EventAttendee().setEmail(attendee));
        }
        event.setAttendees(attendees);

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";

        event = googleConnector.calendarService().events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        return new CalendarResponse(startDate, endDateTime);
    }

    public DateTime getStartDate(Date startDate, Date endDate, Long duration) throws IOException {
        org.joda.time.DateTime jodaStartDateTime = new org.joda.time.DateTime(startDate);
        org.joda.time.DateTime jodaStopDateTime = new org.joda.time.DateTime(endDate);
        Duration durationInMinutes = Duration.standardMinutes(duration);
        Interval firstInterval = Scheduler.getOneTimeSlotBetweenDatesOfLength(getFreeBusy(startDate, endDate), durationInMinutes, jodaStartDateTime, jodaStopDateTime);
        Date eventStartDate = firstInterval.getStart().toDate();
        return new DateTime(eventStartDate);
    }

    public Vector<Interval> getFreeBusy(Date startDate, Date endDate) throws IOException {
        FreeBusyRequest freeBusyRequest = new FreeBusyRequest();
        freeBusyRequest.setItems(getFreeBusyItems(getAllCalendarId()));
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE_SPECIFIER);
        DateTime startTime = new DateTime(startDate, timeZone);
        DateTime endTime = new DateTime(endDate, timeZone);
        freeBusyRequest.setTimeZone(TIME_ZONE_SPECIFIER);
        freeBusyRequest.setTimeMin(startTime);
        freeBusyRequest.setTimeMax(endTime);
        Calendar.Freebusy.Query freeBusyQuery = googleConnector.calendarService().freebusy().query(freeBusyRequest);
        FreeBusyResponse freeBusyResponse = freeBusyQuery.execute();
        System.out.println(freeBusyResponse.toString());
        return getBusyIntervals(freeBusyResponse);
    }

    private Vector<Interval> getBusyIntervals(FreeBusyResponse freeBusyResponse) {
        Vector<TimePeriod> busyTimePeriods = new Vector<>();
        for (String calendarId : freeBusyResponse.getCalendars().keySet()) {
            busyTimePeriods.addAll(freeBusyResponse.getCalendars().get(calendarId).getBusy());
        }
        return Converter.covertTimePeriodsToIntervals(busyTimePeriods);
    }

    private List<String> getAllCalendarId() throws IOException {
        List<String> calendarsIds = new ArrayList<>();
        CalendarList calendarList = googleConnector.calendarService().calendarList().list().execute();

        for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
            calendarsIds.add(calendarListEntry.getId());
        }
        return calendarsIds;
    }

    private List<FreeBusyRequestItem> getFreeBusyItems(List<String> calendarsIds) {
        List<FreeBusyRequestItem> freeBusyRequestItems = new ArrayList<>();
        for (String id : calendarsIds) {
            freeBusyRequestItems.add(new FreeBusyRequestItem().set("id", id));
        }
        return freeBusyRequestItems;
    }


}
