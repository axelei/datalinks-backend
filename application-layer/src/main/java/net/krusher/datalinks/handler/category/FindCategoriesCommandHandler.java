package net.krusher.datalinks.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.model.page.Category;

import java.util.List;

@ApplicationScoped
public class FindCategoriesCommandHandler {

    private final SearchService searchService;

    @Inject
    public FindCategoriesCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<Category> handler(String query) {
        return searchService.searchCategories(query);
    }


}
