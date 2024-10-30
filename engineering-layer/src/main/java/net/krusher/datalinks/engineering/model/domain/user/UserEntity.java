package net.krusher.datalinks.engineering.model.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.model.user.UserLevel;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Table(name = "USERS", indexes = {
        @Index(name = "IDX_USER_USERNAME", columnList = "username")
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

    private String password;
    private String salt;
    private UUID activationToken;
    private UUID resetToken;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.id = UUID.randomUUID();
        this.creationDate = Instant.now();
    }
}
