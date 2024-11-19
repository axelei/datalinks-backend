package net.krusher.datalinks.handler.page;

import lombok.Builder;
import lombok.Data;
import net.krusher.datalinks.model.page.Category;

import java.util.UUID;

@Data
@Builder
public class PostPageCommand {
    private String title;
    private String content;
    private Category[] categories;
    private UUID loginTokenId;
    private String ip;
}
