package net.krusher.datalinks.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.krusher.datalinks.model.page.Category;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostPageModel {
    private String content;
    private Category[] categories;
}
