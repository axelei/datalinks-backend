package net.krusher.datalinks.handler.category;

import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.handler.common.SlugifyProvider;
import net.krusher.datalinks.model.page.PageShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindCategoryPagesCommandHandler {

    private final CategoryService categoryService;

    @Autowired
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
