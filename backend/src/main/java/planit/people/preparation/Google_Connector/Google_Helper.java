package planit.people.preparation.Google_Connector;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import org.joda.time.Interval;
import org.joda.time.Minutes;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Responses.CalendarResponse;
import planit.people.preparation.Scheduling.Converter;
import planit.people.preparation.Scheduling.Scheduler;

import java.io.IOException;
import java.util.*;

public class Google_Helper {
    private static String timeZone;
    private final Google_Connector google_connector = new Google_Connector();
    public String refresh_token;

    public Google_Helper(String code, Boolean refresh_token) {
        if (refresh_token) {
            google_connector.setRefresh_token(code);
        } else {
            google_connector.setCode(code);
        }
    }

    public Userinfo get_user_info() throws IOException {
        Oauth2 oauth2 = google_connector.oauth2_service();
        System.out.println("oauth2: " + oauth2.userinfo().get().execute());
        return oauth2.userinfo().get().execute();
    }


    public String getRefreshToken() {
        return google_connector.getRefresh_token();
    }

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail) throws IOException {
        Event event = new Event()
                .setSummary(newEventDetail.summary())
                .setLocation(newEventDetail.location())
                .setDescription(newEventDetail.description());
        DateTime startDate = getStartDate(newEventDetail.start_date(), newEventDetail.end_date(), newEventDetail.duration());
        System.out.println("Start Date: " + startDate);
        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
                //TODO: STORE TIME ZONE
                .setTimeZone(timeZone);
        event.setStart(start);

        DateTime endDateTime = new DateTime(startDate.getValue() + (newEventDetail.duration() * 60 * 1000));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone(timeZone);
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

        event = google_connector.calendar_service().events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        return new CalendarResponse(startDate, endDateTime);
    }

    public DateTime getStartDate(Date start_date, Date end_date, Long duration) throws IOException {
        return Scheduler.getStartTime(getFreeBusy(start_date, end_date), duration, start_date, end_date);
    }

    public Vector<Interval> getFreeBusy(Date startDate, Date endDate) throws IOException {
        FreeBusyRequest req = new FreeBusyRequest();
        req.setItems(getFreeBusyItems(getAllCalendarId()));
        TimeZone tz = TimeZone.getTimeZone(timeZone);
        DateTime startTime = new DateTime(startDate, tz);
        DateTime endTime = new DateTime(endDate, tz);
        req.setTimeZone(timeZone);
        req.setTimeMin(startTime);
        req.setTimeMax(endTime);
        Calendar.Freebusy.Query fbq = google_connector.calendar_service().freebusy().query(req);
        FreeBusyResponse fbresponse = fbq.execute();
        System.out.println(fbresponse.toString());
        return getBusyIntervals(fbresponse);
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
        CalendarList calendarList = google_connector.calendar_service().calendarList().list().execute();

        for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
            if (calendarListEntry.isPrimary()) {
                timeZone = calendarListEntry.getTimeZone();
            }
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
