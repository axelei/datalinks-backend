package net.krusher.datalinks.handler.user;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetUserCommand {
    private String username;
    private String userToken;
}
