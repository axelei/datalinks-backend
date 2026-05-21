package net.krusher.datalinks.web.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RegisterForReflection
public class UpdateUploadRequestModel {
    private String filename;
    private String description;
    private UUID loginToken;
    private String ip;
}
