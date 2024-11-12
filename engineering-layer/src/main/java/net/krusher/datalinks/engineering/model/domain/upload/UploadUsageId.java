package net.krusher.datalinks.engineering.model.domain.upload;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Data
@Getter
@Setter
public class UploadUsageId implements Serializable {
    private UUID uploadId;
    private UUID pageId;
}
