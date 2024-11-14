package net.krusher.datalinks.handler.page;

import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.model.page.PageShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchCommandHandler {

    private final PageService pageService;

    @Autowired
    public SearchCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public List<PageShort> handler(SearchPaginationCommand query) {
        query.validate();
        return pageService.search(query.getQuery(), query.getPage(), query.getPageSize());
    }
}