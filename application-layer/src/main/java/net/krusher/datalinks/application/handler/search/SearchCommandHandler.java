package net.krusher.datalinks.application.handler.search;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.application.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.domain.model.search.Foundling;

import java.util.List;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class SearchCommandHandler {

    private final SearchService searchService;


    public List<Foundling> handler(SearchPaginationCommand query) {
        query.validate();
        return searchService.search(query.getQuery(), query.getPage(), query.getPageSize());
    }
}
