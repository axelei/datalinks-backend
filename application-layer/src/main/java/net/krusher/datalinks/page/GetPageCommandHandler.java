package net.krusher.datalinks.page;

import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetPageCommandHandler {

    private final PageService pageService;

    @Autowired
    public GetPageCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public Optional<Page> handler(GetPageCommand getPageCommand) {
        return Optional.ofNullable(pageService.findByTitle(getPageCommand.getTitle()));
    }
}
