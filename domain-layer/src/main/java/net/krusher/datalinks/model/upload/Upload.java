package net.krusher.datalinks.model.upload;

import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.user.UserLevel;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class Upload {
    private UUID id;
    private InputStream inputStream;
    private String filename;
    private String slug;
    private String description;
    private UUID creatorId;
    private String md5;
    private UserLevel editBlock;
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;

}
