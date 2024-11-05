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
    @Value("${application.url}")
    private String applicationUrl;

    public enum SIGNUP_PARAMS {
        NAME,
        ACTIVATION_TOKEN
    }

    @Autowired
    public EmailService(JavaMailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void sendSignupMessage(String to, Map<SIGNUP_PARAMS, String> params, String language) {

        Locale locale = Locale.of(language);
        ResourceBundle labels = ResourceBundle.getBundle(EMAILS_BUNDLE, locale);

        String activationUrl = applicationUrl + "/activate/" + params.get(SIGNUP_PARAMS.ACTIVATION_TOKEN);

        String subject = MessageFormat.format(labels.getString("signup.subject"), params.get(SIGNUP_PARAMS.NAME));
        String body = MessageFormat.format(labels.getString("signup.body"), params.get(SIGNUP_PARAMS.NAME), activationUrl);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(emailFrom);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        emailSender.send(message);

    }
}
