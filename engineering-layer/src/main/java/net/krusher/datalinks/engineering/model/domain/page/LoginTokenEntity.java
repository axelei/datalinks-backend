package net.krusher.datalinks.engineering.model.domain.page;

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
@Table(name = "LOGIN_TOKENS", indexes = {
        @Index(name = "IDX_LOGIN_TOKEN_TOKEN", columnList = "token")
})
public class LoginTokenEntity {

    @Id
    @Column(nullable = false)
    private UUID userId;
    private UUID token;
    private Instant creationDate;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.creationDate = Instant.now();
    }
}
