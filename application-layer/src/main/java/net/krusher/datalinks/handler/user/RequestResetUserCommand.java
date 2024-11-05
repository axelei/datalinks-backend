package net.krusher.datalinks.handler.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RequestResetUserCommand {
    private String username;
    private String email;
}
