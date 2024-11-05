package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
@Table(name = "RESET_TOKENS", indexes = {
        @Index(name = "IDX_USER_ID", columnList = "userId"),
})
public class ResetTokenEntity {

    @Id
    @Column(nullable = false)
    private UUID resetToken;
    @Column(nullable = false)
    private UUID userId;
    private Instant creationDate;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.resetToken = UUID.randomUUID();
        this.creationDate = Instant.now();
    }
}
