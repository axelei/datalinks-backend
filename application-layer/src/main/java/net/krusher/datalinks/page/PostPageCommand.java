package net.krusher.datalinks.page;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class PostPageCommand {
    private String title;
    private String content;
    private UUID loginTokenId;
}
