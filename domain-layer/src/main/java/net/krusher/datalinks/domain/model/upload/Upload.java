package net.krusher.datalinks.domain.model.upload;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import net.krusher.datalinks.domain.model.user.UserLevel;

import java.io.InputStream;
import java.time.Instant;
import java.util.UUID;

@Builder
@Data
@RegisterForReflection
@AllArgsConstructor
@NoArgsConstructor
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
    @JsonIgnore
    private UUID creatorId;
    private String md5;
    private UserLevel editBlock;
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;

}
