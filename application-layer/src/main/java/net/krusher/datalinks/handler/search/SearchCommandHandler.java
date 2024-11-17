package net.krusher.datalinks.handler.search;

import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.model.search.Foundling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCommandHandler {

    private final SearchService searchService;

    @Autowired
    public SearchCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<Foundling> handler(SearchPaginationCommand query) {
        query.validate();
        return searchService.search(query.getQuery(), query.getPage(), query.getPageSize());
    }
}