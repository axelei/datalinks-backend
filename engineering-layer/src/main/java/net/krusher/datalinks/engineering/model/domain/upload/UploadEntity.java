package net.krusher.datalinks.engineering.model.domain.upload;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.model.search.Foundable;
import net.krusher.datalinks.model.search.Foundling;
import net.krusher.datalinks.model.user.UserLevel;
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
@Table(name = "UPLOADS", indexes = {
        @Index(name = "IDX_UPLOAD_SLUG", columnList = "slug", unique = true),
        @Index(name = "IDX_UPLOAD_CREATOR_ID", columnList = "creatorId"),
})
public class UploadEntity implements Foundable {

    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    @FullTextField(analyzer = "edgeNGramAnalyzer", searchAnalyzer = "edgeNGramAnalyzer")
    private String filename;
    @Column(nullable = false)
    private String slug;
    @FullTextField
    private String description;
    @Enumerated(EnumType.STRING)
    private UserLevel editBlock;
    @Enumerated(EnumType.STRING)
    private UserLevel readBlock;
    @Column(columnDefinition = "CHAR(32)", nullable = false)
    private String md5;
    private Instant creationDate;
    private Instant modifiedDate;
    private String ipCreator;
    private String ipModifier;
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

    @Override
    public Foundling toFoundling() {
        return Foundling.builder()
                .id(id)
                .title(filename)
                .content(description)
                .type(Foundling.FoundlingType.UPLOAD)
                .build();
    }
}
