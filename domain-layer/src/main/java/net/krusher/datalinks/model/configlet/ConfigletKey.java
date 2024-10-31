package net.krusher.datalinks.model.configlet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.krusher.datalinks.model.user.UserLevel;

@Getter
@AllArgsConstructor
public enum ConfigletKey {
    DEFAULT_BLOCK(UserLevel.GUEST.name()),
    TOKEN_EXPIRATION("3600"),
    ;

    private final String defaultValue;

}
