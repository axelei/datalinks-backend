package net.krusher.datalinks.model.user;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class LoginToken {
    private UUID userId;
    private UUID token;
}