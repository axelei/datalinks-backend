package net.krusher.datalinks.domain.model.search;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

@Builder
@Data
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
