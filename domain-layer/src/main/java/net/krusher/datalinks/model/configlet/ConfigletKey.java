package net.krusher.datalinks.model.configlet;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfigletKey {
    SITE_TITLE("Datalinks"),
    SITE_DESCRIPTION("Planetary datalinks"),
    SITE_IMAGE("https://www.nasa.gov/sites/default/files/thumbnails/image/nasa-logo-web-rgb.png"),
    ;

    private final String defaultValue;

}
