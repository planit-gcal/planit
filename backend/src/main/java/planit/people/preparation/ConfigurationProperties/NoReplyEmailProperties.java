package planit.people.preparation.ConfigurationProperties;

import org.checkerframework.checker.propkey.qual.PropertyKey;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("no-reply-email")
public class NoReplyEmailProperties{
    /**
     * The PlanIt sender email
     */
    private String email;
    /**
     * The app password generated by google
     */
    private String password;
    /**
     * The emil used to forward users emails
     */
    private String replyToEmail;


    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }

    public String getReplyToEmail() {
        return replyToEmail;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setReplyToEmail(String replyToEmail) {
        this.replyToEmail = replyToEmail;
    }
}