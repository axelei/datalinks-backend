package net.krusher.datalinks.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.ReCaptchaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@ConfigurationProperties(prefix = "google.recaptcha.key")
@Setter
public class CaptchaHelper {

    private String site;
    private String secret;

    private final ObjectMapper objectMapper;

    private final String SERVICE_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    public CaptchaHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public boolean checkCaptcha(String response, String remoteIP) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            ReCaptchaModel reCaptchaModel = ReCaptchaModel.builder()
                    .response(response)
                    .remoteIp(remoteIP)
                    .secret(secret)
                    .build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(SERVICE_URL))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(reCaptchaModel)))
                    .build();
            HttpResponse<String> reCaptchaResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            return reCaptchaResponse.body().contains("\"success\": true");
        } catch (InterruptedException | IOException e) {
            throw new EngineException(ErrorType.CAPTCHA_ERROR, e);
        }
    }

}
