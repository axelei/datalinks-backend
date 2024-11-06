package net.krusher.datalinks.engineering.model.domain.email;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Service
public class EmailService {

    public static final String EMAILS_BUNDLE = "email.emails";

    private final JavaMailSender emailSender;

    @Setter
    @Value("${spring.mail.from}")
    private String emailFrom;
    @Setter
    @Value("${application.sitename}")
    private String siteName;
    @Setter
    @Value("${application.url}")
    private String applicationUrl;

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSignupMessage(String to, Map<SignupParams, String> params, String language) {
        ResourceBundle labels = getResourceBundle(language);

        String activationUrl = applicationUrl + "/activateUser/" + params.get(SignupParams.ACTIVATION_TOKEN);

        String subject = MessageFormat.format(labels.getString("signup.subject"), siteName, params.get(SignupParams.NAME));
        String body = MessageFormat.format(labels.getString("signup.body"), params.get(SignupParams.NAME), siteName, activationUrl);

        emailSender.send(createMessage(to, subject, body));
    }

    public void sendRequestResetMessage(String to, Map<RequestResetTokenParams, String> params, String language) {
        ResourceBundle labels = getResourceBundle(language);

        String resetUrl = applicationUrl + "/resetPassword/" + params.get(RequestResetTokenParams.RESET_TOKEN);

        String subject = MessageFormat.format(labels.getString("requestReset.subject"), siteName);
        String body = MessageFormat.format(labels.getString("requestReset.body"), params.get(RequestResetTokenParams.NAME), siteName, resetUrl);

        emailSender.send(createMessage(to, subject, body));
    }

    public void sendResetMessage(String to, Map<ResetParams, String> params, String language) {
        ResourceBundle labels = getResourceBundle(language);

        String subject = MessageFormat.format(labels.getString("reset.subject"), siteName);
        String body = MessageFormat.format(labels.getString("reset.body"), params.get(ResetParams.NAME), siteName, params.get(ResetParams.NEW_PASSWORD));

        emailSender.send(createMessage(to, subject, body));
    }

    public void sendPasswordChanged(String to, Map<PasswordChangeParams, String> params, String language) {
        ResourceBundle labels = getResourceBundle(language);

        String subject = MessageFormat.format(labels.getString("passwordChanged.subject"), siteName);
        String body = MessageFormat.format(labels.getString("passwordChanged.body"), params.get(PasswordChangeParams.NAME), siteName);

        emailSender.send(createMessage(to, subject, body));
    }

    private SimpleMailMessage createMessage(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

    private ResourceBundle getResourceBundle(String language) {
        return ResourceBundle.getBundle(EmailService.EMAILS_BUNDLE, Locale.forLanguageTag(language));
    }
}
