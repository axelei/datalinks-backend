package net.krusher.datalinks.application.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.application.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.application.handler.common.SlugifyProvider;
import net.krusher.datalinks.domain.model.page.PageShort;

import java.util.List;

@ApplicationScoped
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class FindCategoryPagesCommandHandler {

    private final CategoryService categoryService;


    public List<PageShort> handler(SearchPaginationCommand searchPaginationCommand) {
        return categoryService.getPagesByCategorySlug(
                SlugifyProvider.SLUGIFY.slugify(searchPaginationCommand.getQuery()),
                searchPaginationCommand.getPage(),
                searchPaginationCommand.getPageSize());
    }


}
