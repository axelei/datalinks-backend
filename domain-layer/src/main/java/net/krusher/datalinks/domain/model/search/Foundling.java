package net.krusher.datalinks.domain.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
@RegisterForReflection
public class Foundling {

    public enum FoundlingType {
        PAGE,
        UPLOAD,
        CATEGORY,
        USER,
    }

    @JsonIgnore
    private UUID id;
    private String title;
    private String content;
    private FoundlingType type;

}
