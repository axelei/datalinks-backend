package net.krusher.datalinks.web.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.domain.model.page.Category;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostPageModel {
    private String content;
    private Category[] categories;
}
