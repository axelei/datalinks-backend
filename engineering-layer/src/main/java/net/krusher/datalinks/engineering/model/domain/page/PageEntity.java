package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.model.user.UserLevel;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "PAGES", indexes = {
        @Index(name = "IDX_PAGE_TITLE", columnList = "title")
})
public class PageEntity {

    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String title;
    @ManyToOne
    private UserEntity author;
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;
    @ManyToMany
    private Set<CategoryEntity> categories;
    @Enumerated(EnumType.STRING)
    private UserLevel block;
    private Instant creationDate;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.id = UUID.randomUUID();
        this.creationDate = Instant.now();
    }
}
