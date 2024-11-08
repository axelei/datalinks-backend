package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
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
        @Index(name = "IDX_PAGE_SLUG", columnList = "slug"),
        @Index(name = "IDX_PAGE_CREATOR_ID", columnList = "creatorId"),
        @Index(name = "IDX_PAGE_CREATION_DATE", columnList = "creationDate"),
        @Index(name = "IDX_PAGE_MODIFIED_DATE", columnList = "modifiedDate")
})
public class PageEntity {

    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String slug;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;
    @ManyToMany(fetch = FetchType.LAZY)
    private Set<CategoryEntity> categories;
    @Enumerated(EnumType.STRING)
    private UserLevel editBlock;
    @Enumerated(EnumType.STRING)
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;
    private UUID creatorId;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.id = UUID.randomUUID();
        this.creationDate = Instant.now();
    }

    @PreUpdate
    protected void setDefaultsOnUpdate() {
        this.modifiedDate = Instant.now();
    }
}
