package planit.people.preparation.ConfigurationProperties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "integrations")
public class IntegrationProperties {
    private String googleTokenRedirectUri;

    public String getGoogleTokenRedirectUri() {
        return googleTokenRedirectUri;
    }

    public void setGoogleTokenRedirectUri(String googleTokenRedirectUri) {
        this.googleTokenRedirectUri = googleTokenRedirectUri;
    }

}
