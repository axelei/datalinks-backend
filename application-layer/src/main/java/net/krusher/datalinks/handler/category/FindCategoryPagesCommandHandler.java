package net.krusher.datalinks.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.handler.common.SlugifyProvider;
import net.krusher.datalinks.model.page.PageShort;

import java.util.List;

@ApplicationScoped
public class FindCategoryPagesCommandHandler {

    private final CategoryService categoryService;

    @Inject
    public FindCategoryPagesCommandHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<PageShort> handler(SearchPaginationCommand searchPaginationCommand) {
        return categoryService.getPagesByCategorySlug(
                SlugifyProvider.SLUGIFY.slugify(searchPaginationCommand.getQuery()),
                searchPaginationCommand.getPage(),
                searchPaginationCommand.getPageSize());
    }


}
