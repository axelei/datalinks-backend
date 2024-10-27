package net.krusher.datalinks.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig
{

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return switch (activeProfile) {
            case "dev" -> new DevConfig();
            case "prod" -> new ProdConfig();
            default -> new WebMvcConfigurer() {
            };
        };
    }
}