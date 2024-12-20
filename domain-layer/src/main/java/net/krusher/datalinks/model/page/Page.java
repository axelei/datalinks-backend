package net.krusher.datalinks.model.page;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;

import java.time.Instant;
import java.util.HashSet;
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
    private String summary;
    private Set<Category> categories;
    private UserLevel editBlock;
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;
    private User creator;
}
