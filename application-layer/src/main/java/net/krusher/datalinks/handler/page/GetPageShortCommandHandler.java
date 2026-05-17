package net.krusher.datalinks.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.PageShort;

import java.util.Optional;

import static net.krusher.datalinks.handler.common.SlugifyProvider.SLUGIFY;

@ApplicationScoped
public class GetPageShortCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    @Inject
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
