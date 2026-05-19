package net.krusher.datalinks.engineering.model.domain.email;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.Setter;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@ApplicationScoped
public class EmailService {

    public static final String EMAILS_BUNDLE = "email.emails";

    @Setter
    @ConfigProperty(name = "quarkus.mailer.from")
    String emailFrom;
    @Setter
    @ConfigProperty(name = "application.sitename")
    String siteName;
    @Setter
    @ConfigProperty(name = "application.url")
    String applicationUrl;

    @Inject Mailer mailer;

    public void sendSignupMessage(String to, Map<SignupParams, String> params, String language) {
        ResourceBundle labels = getResourceBundle(language);

        String activationUrl = applicationUrl + "/activateUser/" + params.get(SignupParams.ACTIVATION_TOKEN);

        String subject = MessageFormat.format(labels.getString("signup.subject"), siteName, params.get(SignupParams.NAME));
        String body = MessageFormat.format(labels.getString("signup.body"), params.get(SignupParams.NAME), siteName, activationUrl);

        mailer.send(createMessage(to, subject, body));
    }

    public void sendRequestResetMessage(String to, Map<RequestResetTokenParams, String> params, String language) {
        ResourceBundle labels = getResourceBundle(language);

        String resetUrl = applicationUrl + "/resetPassword/" + params.get(RequestResetTokenParams.RESET_TOKEN);

        String subject = MessageFormat.format(labels.getString("requestReset.subject"), siteName);
        String body = MessageFormat.format(labels.getString("requestReset.body"), params.get(RequestResetTokenParams.NAME), siteName, resetUrl);

        mailer.send(createMessage(to, subject, body));
    }

    public void sendResetMessage(String to, Map<ResetParams, String> params, String language) {
        ResourceBundle labels = getResourceBundle(language);

        String subject = MessageFormat.format(labels.getString("reset.subject"), siteName);
        String body = MessageFormat.format(labels.getString("reset.body"), params.get(ResetParams.NAME), siteName, params.get(ResetParams.NEW_PASSWORD));

        mailer.send(createMessage(to, subject, body));
    }

    public void sendPasswordChanged(String to, Map<PasswordChangeParams, String> params, String language) {
        ResourceBundle labels = getResourceBundle(language);

        String subject = MessageFormat.format(labels.getString("passwordChanged.subject"), siteName);
        String body = MessageFormat.format(labels.getString("passwordChanged.body"), params.get(PasswordChangeParams.NAME), siteName);

        mailer.send(createMessage(to, subject, body));
    }

    private Mail createMessage(String to, String subject, String body) {
        return Mail.withText(to, subject, body).setFrom(emailFrom);
    }

    private ResourceBundle getResourceBundle(String language) {
        return ResourceBundle.getBundle(EmailService.EMAILS_BUNDLE, Locale.forLanguageTag(language));
    }
}
