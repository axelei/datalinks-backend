package net.krusher.datalinks.model.configlet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.krusher.datalinks.model.user.UserLevel;

@Getter
@AllArgsConstructor
public enum ConfigletKey {
    //SITE_TITLE("Datalinks"),
    //SITE_DESCRIPTION("Planetary datalinks"),
    //SITE_IMAGE("https://www.nasa.gov/sites/default/files/thumbnails/image/nasa-logo-web-rgb.png"),
    DEFAULT_BLOCK(UserLevel.GUEST.name()),
    ;

    private final String defaultValue;

}
