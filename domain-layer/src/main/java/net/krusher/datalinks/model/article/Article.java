package net.krusher.datalinks.model.article;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Article {
    private String title;
    private String content;
}
