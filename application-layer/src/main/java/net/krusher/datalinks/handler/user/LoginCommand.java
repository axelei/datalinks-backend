package net.krusher.datalinks.handler.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginCommand {
    private String username;
    private String password;
}
