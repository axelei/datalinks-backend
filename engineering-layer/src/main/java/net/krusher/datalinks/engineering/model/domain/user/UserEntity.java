package net.krusher.datalinks.engineering.model.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.model.user.UserLevel;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "USERS", indexes = {
        @Index(name = "IDX_USER_USERNAME", columnList = "username"),
        @Index(name = "IDX_USER_ACTIVATION_TOKEN", columnList = "activationToken")
})
public class UserEntity {

    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserLevel level;
    private String email;
    private String name;
    private Instant creationDate;
    @Column(columnDefinition = "VARCHAR(5)")
    private String language;

    @Column(columnDefinition = "CHAR(64)")
    private String password;
    @Column(columnDefinition = "CHAR(8)")
    private String salt;
    private UUID activationToken;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.id = UUID.randomUUID();
        this.creationDate = Instant.now();
    }
}
