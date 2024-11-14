package net.krusher.datalinks.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Builder
@Data
public class Edit {

    private UUID id;
    @JsonIgnore
    private UUID pageId;
    private String title;
    private String content;
    @JsonIgnore
    private String ip;
    private Instant date;
    @JsonIgnore
    private UUID userId;
    private String username;
}
