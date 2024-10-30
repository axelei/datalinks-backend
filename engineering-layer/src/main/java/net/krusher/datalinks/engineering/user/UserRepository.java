package net.krusher.datalinks.engineering.user;

import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserRepository {

    public Optional<User> get(String username) {
        return Optional.of(User.builder()
                .id(UUID.randomUUID())
                .username(username)
                .email("email")
                .level(UserLevel.USER)
                .creationDate(Instant.now())
                .build());
    }

}
