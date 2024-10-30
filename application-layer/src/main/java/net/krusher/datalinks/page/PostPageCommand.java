package net.krusher.datalinks.page;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostPageCommand {
    private String title;
    private String content;
    private String userToken;
}
