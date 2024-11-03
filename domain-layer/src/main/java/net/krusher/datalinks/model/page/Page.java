package net.krusher.datalinks.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.user.UserLevel;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class Page {

    @JsonIgnore
    private UUID id;
    private String slug;
    private String title;
    private String content;
    private Set<Category> categories;
    private UserLevel editBlock;
    private UserLevel readBlock;
    private Instant creationDate;
}
