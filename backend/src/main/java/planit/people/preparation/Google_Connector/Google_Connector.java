package planit.people.preparation.Google_Connector;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponseException;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
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
import com.google.api.services.oauth2.model.Userinfo;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.UserCredentials;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Google_Connector {
    /**
     * Application name.
     */
    private static final String APPLICATION_NAME = "Planit";
    /**
     * Global instance of the JSON factory.
     */
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String CREDENTIALS_RESOURCE_NAME = "/client_secret.json";
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
            clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(Google_Connector.class.getResourceAsStream(CREDENTIALS_RESOURCE_NAME)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final String clientId = clientSecrets.getDetails().getClientId();
    private static final String clientSecret = clientSecrets.getDetails().getClientSecret();

    private String code;
    private String refresh_token;


    public Google_Connector(String refresh_token) {
        this.refresh_token = refresh_token;

    }

    public Google_Connector() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        setRefreshAndExpiry();
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public HttpRequestInitializer getCredentials() throws IOException {
        var credentials = UserCredentials.newBuilder()
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setRefreshToken(this.refresh_token)
                .build();

        return new HttpCredentialsAdapter(credentials);
    }

    public Calendar calendar_service() throws IOException {
        return new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public Oauth2 oauth2_service() throws IOException {
        return new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials())
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private void setRefreshAndExpiry() {
        try {
            String clientId = clientSecrets.getDetails().getClientId();
            String clientSecret = clientSecrets.getDetails().getClientSecret();
            GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                    clientId, clientSecret,
                    this.code, "https://planit-custom-domain.loca.lt/")
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

    public static void main(String[] args) throws IOException {
        Google_Connector google_connector = new Google_Connector("<TOKEN>");
        Calendar service =
                new Calendar.Builder(HTTP_TRANSPORT, JSON_FACTORY, google_connector.getCredentials())
                        .setApplicationName(APPLICATION_NAME)
                        .build();
        Oauth2 oauth2 = new Oauth2.Builder(HTTP_TRANSPORT, JSON_FACTORY, google_connector.getCredentials())
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
