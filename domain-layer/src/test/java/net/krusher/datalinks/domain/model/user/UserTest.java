package net.krusher.datalinks.domain.model.user;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserTest {

    @Test
    void useNamePrefersNameWhenPresent() {
        User u = User.builder()
                .username("jdoe")
                .name("John")
                .creationDate(Instant.now())
                .build();

        assertEquals("John", u.useName());
    }

    @Test
    void useNameFallsBackToUsername() {
        User u = User.builder()
                .username("jdoe")
                .build();
        assertEquals("jdoe", u.useName());
    }
}
