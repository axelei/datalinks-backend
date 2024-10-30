package net.krusher.datalinks.model.user;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class User {

    private UUID id;
    private String username;
    private UserLevel level;
    private String email;
    private String name;
/*
    @JsonIgnore private String password;
    @JsonIgnore private String salt;
    @JsonIgnore private UUID activationToken;
    @JsonIgnore private UUID resetToken;

 */

}
