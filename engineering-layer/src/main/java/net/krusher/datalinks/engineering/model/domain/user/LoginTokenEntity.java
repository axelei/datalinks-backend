package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "LOGIN_TOKENS")
public class LoginTokenEntity {

    @Id
    @Column(nullable = false)
    private UUID loginToken;
    @Column(nullable = false)
    private UUID userId;
    private Instant creationDate;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.creationDate = Instant.now();
    }
}
