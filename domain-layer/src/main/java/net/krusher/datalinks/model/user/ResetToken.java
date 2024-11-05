package net.krusher.datalinks.model.user;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class ResetToken {
    private UUID userId;
    private UUID resetToken;
    private Instant creationDate;
}
