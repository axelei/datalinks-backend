package net.krusher.datalinks.handler.page;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PostPageCommand {
    private String title;
    private String content;
    private String[] categories;
    private UUID loginTokenId;
}
