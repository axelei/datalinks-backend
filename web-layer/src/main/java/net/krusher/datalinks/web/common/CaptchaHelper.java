package net.krusher.datalinks.web.common;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.Setter;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@ApplicationScoped
@Setter
public class CaptchaHelper {

    @ConfigProperty(name = "google.recaptcha.key.site")
    String site;

    @ConfigProperty(name = "google.recaptcha.key.secret")
    String secret;

    private static final String SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

    public boolean checkCaptcha(String response, String remoteIP) {
        try {
            URL url = new URI(String.format(
                    SERVICE_URL,
                    secret, response, remoteIP)).toURL();
            try (InputStream in = url.openStream()) {
                String googleResponse = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                return googleResponse.contains("\"success\": true");
            }
        } catch (IOException | URISyntaxException e) {
            throw new EngineException(ErrorType.CAPTCHA_ERROR, e);
        }
    }

}
