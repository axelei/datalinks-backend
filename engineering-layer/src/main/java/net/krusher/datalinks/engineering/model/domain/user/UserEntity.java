package net.krusher.datalinks.engineering.model.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.model.search.Foundable;
import net.krusher.datalinks.model.search.Foundling;
import net.krusher.datalinks.model.user.UserLevel;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.time.Instant;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Indexed
@Table(name = "USERS", indexes = {
        @Index(name = "IDX_USER_USERNAME", columnList = "username"),
        @Index(name = "IDX_USER_ACTIVATION_TOKEN", columnList = "activationToken")
})
public class UserEntity implements Foundable {

    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    @FullTextField(analyzer = "edgeNGramAnalyzer", searchAnalyzer = "edgeNGramAnalyzer")
    private String username;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private UserLevel level;
    private String email;
    @FullTextField(analyzer = "edgeNGramAnalyzer", searchAnalyzer = "edgeNGramAnalyzer")
    private String name;
    private Instant creationDate;
    @Column(columnDefinition = "VARCHAR(5)")
    @ColumnDefault("'en-GB'")
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

    @Override
    public Foundling toFoundling() {
        return Foundling.builder()
                .id(id)
                .title(this.getUsername())
                .type(Foundling.FoundlingType.USER)
                .build();
    }
}
