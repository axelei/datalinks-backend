package net.krusher.datalinks.handler.page;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.page.PageShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static net.krusher.datalinks.handler.common.SlugifyProvider.SLUGIFY;

@Service
public class GetPageShortCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    @Autowired
    public GetPageShortCommandHandler(PageService pageService, UserHelper userHelper) {
        this.pageService = pageService;
        this.userHelper = userHelper;
    }

    public Optional<PageShort> handler(GetPageCommand getPageCommand) {
        String slug = SLUGIFY.slugify(getPageCommand.getTitle());
        Optional<PageShort> page = pageService.findShortBySlug(slug);
        if (page.isPresent() && !userHelper.userCanRead(page.get(), getPageCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't read this page");
        }
        return page;
    }
}
