package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.model.search.Foundable;
import net.krusher.datalinks.model.search.Foundling;
import net.krusher.datalinks.model.user.UserLevel;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Indexed
@Table(name = "PAGES", indexes = {
        @Index(name = "IDX_PAGE_SLUG", columnList = "slug"),
        @Index(name = "IDX_PAGE_TITLE", columnList = "title"),
        @Index(name = "IDX_PAGE_CREATOR_ID", columnList = "creatorId"),
        @Index(name = "IDX_PAGE_CREATION_DATE", columnList = "creationDate"),
        @Index(name = "IDX_PAGE_MODIFIED_DATE", columnList = "modifiedDate")
}, uniqueConstraints = {
        @UniqueConstraint(name = "PAGE_UNIQUE_SLUG", columnNames = "slug")
})
public class PageEntity implements Foundable {

    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String slug;
    @Column(nullable = false)
    @FullTextField(analyzer = "edgeNGramAnalyzer", searchAnalyzer = "edgeNGramAnalyzer")
    private String title;
    @ColumnDefault("''")
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    @FullTextField
    private String content;
    private String summary;
    @ManyToMany
    private Set<CategoryEntity> categories;
    @Enumerated(EnumType.STRING)
    private UserLevel editBlock;
    @Enumerated(EnumType.STRING)
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;
    @ManyToOne
    @JoinColumn(name = "creator_id", updatable = false)
    private UserEntity creator;

    @PrePersist
    protected void setDefaultsOnCreate() {
        this.id = UUID.randomUUID();
        this.creationDate = Instant.now();
        this.modifiedDate = Instant.now();
        this.summary = summarize(content);
    }

    @PreUpdate
    protected void setDefaultsOnUpdate() {
        this.modifiedDate = Instant.now();
        this.summary = summarize(content);
    }

    public String summarize(String string) {
        return StringUtils.abbreviate(string
                        .replace("</figure>", " ")
                        .replace("</p>", " ")
                        .replaceAll("<[^>]*>", "")
                , 200);
    }

    @Override
    public Foundling toFoundling() {
        return Foundling.builder()
                .id(id)
                .title(title)
                .content(summarize(content))
                .type(Foundling.FoundlingType.PAGE)
                .build();
    }
}
