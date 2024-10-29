package net.krusher.datalinks.page;

import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.page.Page;

import java.util.Set;

public class GetPageCommandHandler {

    public Page handler(String title) {
        return Page.builder()
            .title(title)
            .content("content")
            .categories(Set.of(
                Category.builder().name("category1").build(),
                Category.builder().name("category2").build()
            ))
            .build();
    }
}
