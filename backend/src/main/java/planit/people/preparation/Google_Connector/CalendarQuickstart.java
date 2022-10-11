package planit.people.preparation.Google_Connector;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfo;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;

/* class to demonstarte use of Calendar events list API */
public class CalendarQuickstart {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Planit";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FILE_PATH = "src/main/java/planit/people/preparation/Google_Connector/client_secret.json";


    public static HttpRequestInitializer getCredentials() throws GeneralSecurityException, IOException {
        InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        String clientId = clientSecrets.getDetails().getClientId();
        String clientSecret = clientSecrets.getDetails().getClientSecret();
        String refreshToken = "<REFRESH>"; //Find a secure way to store and load refresh token

        var credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(refreshToken)
                .build();

        return new HttpCredentialsAdapter(credentials);
    }

    public static void main(String... args) throws IOException, GeneralSecurityException {
        // Build a new authorized API client service.
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
        Userinfo userinfo = oauth2.userinfo().get().execute();
        System.out.println("userinfo: " + userinfo);

        // List the next 10 events from the primary calendar.
        DateTime now = new DateTime(System.currentTimeMillis());
        CalendarList calendarList = service.calendarList().list().execute();

        List<CalendarListEntry> items = calendarList.getItems();
        if (items.isEmpty()) {
            System.out.println("No upcoming events found.");
        } else {
            System.out.println("Calendars: ");

            for (CalendarListEntry calendar : items) {
                System.out.printf("%s (%s)\n", calendar.getSummary(), calendar.getId());
                Events events = service.events().list(calendar.getId()).setTimeMin(now).execute();
                List<Event> eventList = events.getItems();
                System.out.println("events for: "+  calendar.getSummary());
                if (eventList.isEmpty()) {
                    System.out.println("No upcoming events found.");
                } else {
                    System.out.println("Upcoming events");
                    for (Event event : eventList) {
                        System.out.printf("%s (%s)\n", event.getSummary(),event.getICalUID());
                    }
                }
            }
        }


    }
}
