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

    private String password;
    private String salt;

}
