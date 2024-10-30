package net.krusher.datalinks.engineering.page;

import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.page.Page;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PageRepository {

    public Page get(String title) {
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
