package net.krusher.datalinks.handler.category;

import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.model.page.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FindCategoriesCommandHandler {

    private final SearchService searchService;

    @Autowired
    public FindCategoriesCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<Category> handler(String query) {
        return searchService.searchCategories(query);
    }


}
