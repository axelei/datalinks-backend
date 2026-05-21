package net.krusher.datalinks.engineering.model.domain.upload;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@IdClass(UploadUsageId.class)
@Table(name = "UPLOAD_USAGE", indexes = {
        @Index(name = "IDX_UPLOAD_USAGE_UPLOAD_ID", columnList = "uploadId"),
        @Index(name = "IDX_UPLOAD_USAGE_PAGE_ID", columnList = "pageId"),
})
public class UploadUsageEntity {

    @Id
    @Column(nullable = false)
    private UUID uploadId;
    @Id
    @Column(nullable = false)
    private UUID pageId;

}
