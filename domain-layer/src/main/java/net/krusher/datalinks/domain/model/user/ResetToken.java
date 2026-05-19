package net.krusher.datalinks.domain.model.user;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
public class ResetToken {
    private UUID userId;
    private UUID resetToken;
    private Instant creationDate;
}
