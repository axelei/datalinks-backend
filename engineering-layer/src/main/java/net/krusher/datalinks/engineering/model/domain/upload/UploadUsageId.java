package net.krusher.datalinks.engineering.model.domain.upload;

import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
public class UploadUsageId implements Serializable {
    private UUID uploadId;
    private UUID pageId;
}
