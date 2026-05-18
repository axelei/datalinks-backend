package net.krusher.datalinks.application.handler.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupCommand {
    private String username;
    private String password;
    private String email;
    private String name;
    private String language;
}
