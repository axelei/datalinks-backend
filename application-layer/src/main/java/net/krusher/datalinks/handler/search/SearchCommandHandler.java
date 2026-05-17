package net.krusher.datalinks.handler.search;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.model.search.Foundling;

import java.util.List;

@ApplicationScoped
public class SearchCommandHandler {

    private final SearchService searchService;

    @Inject
    public SearchCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<Foundling> handler(SearchPaginationCommand query) {
        query.validate();
        return searchService.search(query.getQuery(), query.getPage(), query.getPageSize());
    }
}
