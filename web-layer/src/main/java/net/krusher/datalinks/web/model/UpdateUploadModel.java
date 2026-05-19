package net.krusher.datalinks.web.model;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@RegisterForReflection
@NoArgsConstructor
public class UpdateUploadModel {
    private String filename;
    private String description;
}
