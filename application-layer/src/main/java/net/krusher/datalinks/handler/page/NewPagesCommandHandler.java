package net.krusher.datalinks.handler.page;

import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.page.PageShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class NewPagesCommandHandler {

    private final PageService pageService;

    @Autowired
    public NewPagesCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public List<PageShort> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return pageService.pagesSortBy("creationDate", paginationCommand.getPage(), paginationCommand.getPageSize());
    }
}
