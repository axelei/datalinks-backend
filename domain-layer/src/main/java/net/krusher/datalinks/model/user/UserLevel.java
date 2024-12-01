package net.krusher.datalinks.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
@Getter
public enum UserLevel {
    ADMIN(10),
    LIBRARIAN(8),
    USER(5),
    GUEST(0),
    BANNED(-10),
    ;

    private final int level;

    public static Optional<UserLevel> valueOf(int level) {
        return Arrays.stream(UserLevel.values())
        .filter(userLevel -> userLevel.getLevel() == level)
        .findFirst();
    }
}
