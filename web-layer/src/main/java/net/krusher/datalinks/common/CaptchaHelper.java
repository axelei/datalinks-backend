package net.krusher.datalinks.common;

import lombok.Setter;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
@Setter
public class CaptchaHelper {

    private String site;
    private String secret;

    private final String SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";

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
