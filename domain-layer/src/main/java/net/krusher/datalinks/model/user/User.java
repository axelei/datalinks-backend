package net.krusher.datalinks.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Builder
@Data
public class User {

    @JsonIgnore private UUID id;
    private String username;
    private UserLevel level;
    @JsonIgnore private String email;
    private String name;
    private Instant creationDate;
    private String language;

    @JsonIgnore private String password;
    @JsonIgnore private String salt;
    @JsonIgnore private UUID activationToken;

    public String useName() {
        return Optional.ofNullable(name).orElse(username);
    }

}
