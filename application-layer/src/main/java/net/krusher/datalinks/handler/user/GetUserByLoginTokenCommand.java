package net.krusher.datalinks.handler.user;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetUserByLoginTokenCommand {
    private UUID loginToken;
}
