package net.krusher.datalinks.model.page;

import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;

import java.time.Instant;

@Builder
@Data
public class PageShort {

    private String slug;
    private String title;
    private String summary;
    private UserLevel editBlock;
    private UserLevel readBlock;
    private Instant creationDate;
    private Instant modifiedDate;
    private User creator;
}
