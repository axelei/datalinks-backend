package net.krusher.datalinks.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.handler.common.SlugifyProvider;
import net.krusher.datalinks.model.page.Category;

import java.util.Optional;

@ApplicationScoped
public class GetCategoryCommandHandler {

    private final CategoryService categoryService;

    @Inject
    public GetCategoryCommandHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Optional<Category> handler(String name) {
        return categoryService.getCategoryBySlug(SlugifyProvider.SLUGIFY.slugify(name));
    }
}
