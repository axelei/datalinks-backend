package net.krusher.datalinks.handler.search;

import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.model.search.Foundling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TitleSearchCommandHandler {

    private final SearchService searchService;
    @Autowired
    public TitleSearchCommandHandler(SearchService searchService) {
        this.searchService = searchService;
    }

    public List<Foundling> handler(String query) {
        return searchService.titleSearch(query);
    }
}
