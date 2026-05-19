package net.krusher.datalinks.application.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.application.handler.common.SlugifyProvider;
import net.krusher.datalinks.domain.model.page.Category;

import java.util.Optional;

@ApplicationScoped
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class GetCategoryCommandHandler {

    private final CategoryService categoryService;


    public Optional<Category> handler(String name) {
        return categoryService.getCategoryBySlug(SlugifyProvider.SLUGIFY.slugify(name));
    }
}
