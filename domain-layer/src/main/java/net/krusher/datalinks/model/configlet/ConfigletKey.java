package net.krusher.datalinks.model.configlet;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.krusher.datalinks.model.user.UserLevel;

@Getter
@AllArgsConstructor
public enum ConfigletKey {
    EDIT_LEVEL(UserLevel.USER.name()),
    CREATE_LEVEL(UserLevel.USER.name()),
    DELETE_LEVEL(UserLevel.ADMIN.name()),
    READ_LEVEL(UserLevel.GUEST.name()),
    TOKEN_EXPIRATION("3600"),
    ;

    private final String defaultValue;

}
