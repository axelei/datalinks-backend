package net.krusher.datalinks.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.page.PageShort;

import java.util.List;

@ApplicationScoped
public class NewPagesCommandHandler {

    private final PageService pageService;

    @Inject
    public NewPagesCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public List<PageShort> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return pageService.pagesSortBy("creationDate", paginationCommand.getPage(), paginationCommand.getPageSize());
    }
}
