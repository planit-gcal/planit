package planit.people.preparation.Utils;

import freemarker.template.Configuration;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import planit.people.preparation.ConfigurationProperties.NoReplyEmailProperties;
import planit.people.preparation.Entities.Entity_GoogleAccount;
import planit.people.preparation.Entities.Entity_User;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class EmailHelper {

    /**
     * Load the sender email properties from application properties
     * @return the properties of the sender email
     * @see NoReplyEmailProperties
     */
    private static NoReplyEmailProperties getNoReplyEmailProperties() {
        return SpringContext.getBean(NoReplyEmailProperties.class);
    }

    /**
     * Send an invitation email to the invitee using a preconfigured email address on behalf of the inviter via Java Mail.
     *
     * @param fromAccount google account of the inviter
     * @param toAccount google account of the invitee
     * @param inviteCode generated code of the invite
     * @param configuration the configuration settings of FreeMarker
     * @throws MessagingException in case of any errors with Java Mail
     * @see org.springframework.mail.javamail.JavaMailSender
     * @see EmailHelper#getNoReplyEmailProperties()
     * @see EmailHelper#getEmailVariables(Entity_User, Entity_User, UUID)
     * @see EmailHelper#geContentFromTemplate(Map, Configuration)
     * @see EmailHelper#getSession()
     */
    public static void sendEmail(Entity_GoogleAccount fromAccount, Entity_GoogleAccount toAccount, UUID inviteCode, Configuration configuration) throws MessagingException {
        Address[] replyTo = {new InternetAddress(getNoReplyEmailProperties().getEmail())};
        Map<String, Object> emailVariables = getEmailVariables(fromAccount.getThe_user(), toAccount.getThe_user(), inviteCode);
        MimeMessage mimeMessage = new MimeMessage(getSession());
        mimeMessage.setReplyTo(replyTo);
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setSubject("Invitation: Join Preset");
            mimeMessageHelper.setFrom(getNoReplyEmailProperties().getEmail());
            mimeMessageHelper.setTo(toAccount.getEmail());
            mimeMessageHelper.setText(geContentFromTemplate(emailVariables, configuration), true);
            Transport.send(mimeMessageHelper.getMimeMessage());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /**
     * create an email session authenticated with using the credentials of the sender email.
     * @return an active java email session
     * @see Session
     */
    private static Session getSession() {
        Properties properties = System.getProperties();

        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        return Session.getInstance(properties, new javax.mail.Authenticator() {

            protected PasswordAuthentication getPasswordAuthentication() {

                return new PasswordAuthentication(getNoReplyEmailProperties().getEmail(), getNoReplyEmailProperties().getPassword());

            }

        });

    }

    /**
     * Get the variables to be used in the email template from the inviter PlanItUser and invitee PlanItUser
     * @param fromUser inviter PlanIt User
     * @param toUser invitee PlanIt User
     * @param inviteCode the invite code
     * @return a map containing the name of the variable as a key and the value of the variable
     */
    private static Map<String, Object> getEmailVariables(Entity_User fromUser, Entity_User toUser, UUID inviteCode) {
        return new HashMap<>() {{
            put("senderName", fromUser.getName() + " " + fromUser.getSurname());
            put("receiverName", toUser.getName() + " " + toUser.getSurname());
            put("inviteCode", inviteCode);
        }};
    }

    /**
     * get the email template populated with the variables from the invitee and inviter.
     * @param model a map containing the variables to be used in the email template
     * @param configuration the configuration settings of FreeMarker
     * @return an HTML email template converted into String
     * @see FreeMarkerTemplateUtils
     */
    private static String geContentFromTemplate(Map<String, Object> model, Configuration configuration) {
        StringBuilder content = new StringBuilder();
        try {
            content.append(FreeMarkerTemplateUtils.processTemplateIntoString(configuration.getTemplate("share-preset-email-template.flth"), model));
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Email: " + content);
        return content.toString();
    }
}
