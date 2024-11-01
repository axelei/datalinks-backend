package net.krusher.datalinks.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class User {

    @JsonIgnore private UUID id;
    private String username;
    private UserLevel level;
    private String email;
    private String name;
    private Instant creationDate;

    @JsonIgnore private String password;
    @JsonIgnore private String salt;
    @JsonIgnore private UUID activationToken;
    @JsonIgnore private UUID resetToken;

}
