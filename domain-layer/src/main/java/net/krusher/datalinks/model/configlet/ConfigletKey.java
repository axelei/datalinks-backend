package net.krusher.datalinks.model.configlet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.krusher.datalinks.model.user.UserLevel;

@Getter
@AllArgsConstructor
public enum ConfigletKey {
    DEFAULT_EDIT_LEVEL(UserLevel.GUEST.name()),
    CREATE_LEVEL(UserLevel.GUEST.name()),
    DELETE_LEVEL(UserLevel.ADMIN.name()),
    TOKEN_EXPIRATION("3600"),
    ;

    private final String defaultValue;

}
