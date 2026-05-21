package net.krusher.datalinks.domain.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@RegisterForReflection
@Builder
@AllArgsConstructor
@NoArgsConstructor
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

    public String displayName() {
        return Optional.ofNullable(name).orElse(username);
    }

}
