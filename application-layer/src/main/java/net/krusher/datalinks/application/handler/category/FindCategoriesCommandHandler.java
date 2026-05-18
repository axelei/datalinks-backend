package net.krusher.datalinks.application.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.domain.model.page.Category;

import java.util.List;

@ApplicationScoped
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class FindCategoriesCommandHandler {

    private final SearchService searchService;


    public List<Category> handler(String query) {
        return searchService.searchCategories(query);
    }


}
