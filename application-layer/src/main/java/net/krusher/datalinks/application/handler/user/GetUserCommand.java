package net.krusher.datalinks.application.handler.user;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetUserCommand {
    private String username;
    private UUID loginToken;
}
