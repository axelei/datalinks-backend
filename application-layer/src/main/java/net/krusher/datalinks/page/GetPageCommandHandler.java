package net.krusher.datalinks.page;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetPageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    @Autowired
    public GetPageCommandHandler(PageService pageService, UserHelper userHelper) {
        this.pageService = pageService;
        this.userHelper = userHelper;
    }

    public Optional<Page> handler(GetPageCommand getPageCommand) {
        Optional<Page> page = pageService.findByTitle(getPageCommand.getTitle());
        if (page.isPresent() && !userHelper.userCanRead(page.get(), getPageCommand.getLoginTokenId())) {
            throw new RuntimeException("User can't read this page");
        }
        return pageService.findByTitle(getPageCommand.getTitle());
    }
}
