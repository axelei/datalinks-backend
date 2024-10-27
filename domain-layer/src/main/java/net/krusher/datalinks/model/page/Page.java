package net.krusher.datalinks.model.page;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Page {
    private String title;
    private String content;
}
