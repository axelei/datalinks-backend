package net.krusher.datalinks.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum UserLevel {
    ADMIN(10),
    USER(5),
    GUEST(0);

    private final int level;
}
