package net.krusher.datalinks.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.page.Edit;

import java.util.List;

@ApplicationScoped
public class RecentChangesCommandHandler {

    private final PageService pageService;

    @Inject
    public RecentChangesCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public List<Edit> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return pageService.editsSortBy("date", paginationCommand.getPage(), paginationCommand.getPageSize());
    }
}
