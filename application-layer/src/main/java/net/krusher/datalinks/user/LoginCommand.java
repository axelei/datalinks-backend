package net.krusher.datalinks.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginCommand {
    private String username;
    private String password;
}
