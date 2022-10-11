package planit.people.preparation.Google_Connector;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.*;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Responses.CalendarResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EventObject;
import java.util.List;

public class Google_Helper {
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
//        CalendarListEntry calendarListEntry = google_connector.calendar_service().calendarList().get("*").execute();

        DateTime startDateTime = new DateTime("2022-10-11T15:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                //TODO: STORE TIME ZONE
                .setTimeZone("Europe/Warsaw");
        event.setStart(start);

        DateTime endDateTime = new DateTime(startDateTime.getValue() + (newEventDetail.duration() * 60 * 100));
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Warsaw");
        event.setEnd(end);

        String[] recurrence = new String[] {"RRULE:FREQ=DAILY;COUNT=1"};
        event.setRecurrence(Arrays.asList(recurrence));

        List<EventAttendee> attendees = new ArrayList<>();
        for (String attendee : newEventDetail.attendees()) {
            attendees.add(new EventAttendee().setEmail(attendee));
        }
        event.setAttendees(attendees);

        EventReminder[] reminderOverrides = new EventReminder[] {
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
        return new CalendarResponse(startDateTime, endDateTime);
    }



}
