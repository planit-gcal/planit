package planit.people.preparation.GoogleConnector;

import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.services.oauth2.Oauth2;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;
import planit.people.preparation.DTOs.DTO_NewEventDetail;
import planit.people.preparation.Responses.CalendarResponse;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.*;


public class GoogleConnector {
    public static final String TIME_ZONE_SPECIFIER = "UTC";
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Planit";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "backend/src/main/java/planit/people/preparation/Google_Connector/client_secret.json";
    private static final NetHttpTransport HTTP_TRANSPORT;

    static {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static final GoogleClientSecrets clientSecrets;

    static {
        try {
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(new FileInputStream(CREDENTIALS_FILE_PATH)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String clientId = clientSecrets.getDetails().getClientId();
    private static final String clientSecret = clientSecrets.getDetails().getClientSecret();

    private String code;
    private String refresh_token;


    public GoogleConnector(String refresh_token) {
        this.refresh_token = refresh_token;

    }

    public GoogleConnector() {

    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        setRefreshAndExpiry();
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public void setRefreshToken(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public HttpRequestInitializer getCredentials() {
        var credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(this.refresh_token)
                .build();

        return new HttpCredentialsAdapter(credentials);
    }

    public Calendar calendarService() {
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Oauth2 oauth2Service() {
        return new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private void setRefreshAndExpiry() {
        try {
            InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            String clientId = clientSecrets.getDetails().getClientId();
            String clientSecret = clientSecrets.getDetails().getClientSecret();
            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                    clientId, clientSecret,
                    this.code, "http://localhost:3000")
                    .execute();
            System.out.println("response: " + response);
            this.refresh_token = response.getRefreshToken();
            System.out.println("Refresh token: " + response.getRefreshToken());
        } catch (TokenResponseException e) {
            if (e.getDetails() != null) {
                System.err.println("Error: " + e.getDetails().getError());
                if (e.getDetails().getErrorDescription() != null) {
                    System.err.println(e.getDetails().getErrorDescription());
                }
                if (e.getDetails().getErrorUri() != null) {
                    System.err.println(e.getDetails().getErrorUri());
                }
            } else {
                System.err.println(e.getMessage());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public CalendarResponse createEvent(DTO_NewEventDetail newEventDetail, DateTime startDate) throws IOException {
        Event event = new Event()
                .setSummary(newEventDetail.summary())
                .setLocation(newEventDetail.location())
                .setDescription(newEventDetail.description());
        EventDateTime start = new EventDateTime()
                .setDateTime(startDate)
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

        event = calendarService().events().insert(calendarId, event).execute();
        System.out.printf("Event created: %s\n", event.getHtmlLink());
        return new CalendarResponse(startDate, endDateTime);
    }

    public FreeBusyResponse getFreeBusy(Date startDate, Date endDate) throws IOException {
        FreeBusyRequest freeBusyRequest = new FreeBusyRequest();
        freeBusyRequest.setItems(getFreeBusyItems());
        TimeZone timeZone = TimeZone.getTimeZone(TIME_ZONE_SPECIFIER);
        DateTime startTime = new DateTime(startDate, timeZone);
        DateTime endTime = new DateTime(endDate, timeZone);
        freeBusyRequest.setTimeZone(TIME_ZONE_SPECIFIER);
        freeBusyRequest.setTimeMin(startTime);
        freeBusyRequest.setTimeMax(endTime);
        Calendar.Freebusy.Query freeBusyQuery = calendarService().freebusy().query(freeBusyRequest);
        FreeBusyResponse freeBusyResponse = freeBusyQuery.execute();
        System.out.println(freeBusyResponse.toString());
        return freeBusyResponse;
    }

    private List<String> getAllCalendarId() throws IOException {
        List<String> calendarsIds = new ArrayList<>();
        CalendarList calendarList = calendarService().calendarList().list().execute();

        for (CalendarListEntry calendarListEntry : calendarList.getItems()) {
            calendarsIds.add(calendarListEntry.getId());
        }
        return calendarsIds;
    }

    private List<FreeBusyRequestItem> getFreeBusyItems() throws IOException {
        List<String> calendarsIds = getAllCalendarId();
        List<FreeBusyRequestItem> freeBusyRequestItems = new ArrayList<>();
        for (String id : calendarsIds) {
            freeBusyRequestItems.add(new FreeBusyRequestItem().set("id", id));
        }
        return freeBusyRequestItems;
    }
}
