package net.krusher.datalinks.model.upload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.user.UserLevel;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class Upload {
    @JsonIgnore
    private UUID id;
    private InputStream inputStream;
    private String filename;
    private String slug;
    private String description;
    @JsonIgnore
    private String ipCreator;
    @JsonIgnore
    private String ipModifier;
    private UUID creatorId;
    private String md5;
    private UserLevel editBlock;
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;

}
