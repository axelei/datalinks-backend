package net.krusher.datalinks.handler.page;

import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.model.page.PageShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TitleSearchCommandHandler {

    private final PageService pageService;
    @Autowired
    public TitleSearchCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public List<PageShort> handler(String query) {
        return pageService.titleSearch(query);
    }
}
