package net.krusher.datalinks.handler.page;

import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class NewPagesCommandHandler {

    private static final Set<Integer> PAGE_SIZES = Set.of(10, 20, 50, 100);

    private final PageService pageService;

    @Autowired
    public NewPagesCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public List<Page> handler(NewPagesCommand newPagesCommand) {
        if (newPagesCommand.getPage() < 0) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Page number must be positive");
        }
        if (!PAGE_SIZES.contains(newPagesCommand.getPageSize())) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Page size must be one of " + PAGE_SIZES);
        }
        return pageService.newPages(newPagesCommand.getPage(), newPagesCommand.getPageSize());
    }
}
