package net.krusher.datalinks.model.page;

import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.user.UserLevel;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
public class Page {
    private UUID id;
    private String title;
    private String content;
    private Set<Category> categories;
    private UserLevel block;
    private Instant creationDate;
}
