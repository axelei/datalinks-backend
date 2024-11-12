package net.krusher.datalinks.engineering.model.domain.upload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
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
@Table(name = "UPLOADS", indexes = {
        @Index(name = "IDX_UPLOAD_SLUG", columnList = "slug", unique = true),
        @Index(name = "IDX_UPLOAD_CREATOR_ID", columnList = "creatorId"),
})
public class UploadEntity {

    @Id
    @Column(nullable = false)
    private UUID id;
    @Column(nullable = false)
    private String filename;
    @Column(nullable = false)
    private String slug;
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
}
