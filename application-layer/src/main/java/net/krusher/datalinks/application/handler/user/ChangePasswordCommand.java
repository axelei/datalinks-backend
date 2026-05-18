package net.krusher.datalinks.application.handler.user;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChangePasswordCommand {
    private UUID loginToken;
    private String password;
}
