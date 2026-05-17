package net.krusher.datalinks.handler.search;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.model.search.Foundling;

import java.util.List;

@ApplicationScoped
public class TitleSearchCommandHandler {

    private final SearchService searchService;

    @Inject
    public TitleSearchCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<Foundling> handler(String query) {
        return searchService.titleSearch(query);
    }
}
