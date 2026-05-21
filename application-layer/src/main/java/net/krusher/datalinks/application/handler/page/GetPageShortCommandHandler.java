package net.krusher.datalinks.application.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.page.PageShort;

import java.util.Optional;

import static net.krusher.datalinks.application.handler.common.SlugifyProvider.SLUGIFY;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class GetPageShortCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;


    public Optional<PageShort> handler(GetPageCommand getPageCommand) {
        String slug = SLUGIFY.slugify(getPageCommand.getTitle());
        Optional<PageShort> page = pageService.findShortBySlug(slug);
        if (page.isPresent() && !userHelper.userCanRead(page.get(), getPageCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't read this page");
        }
        return page;
    }
}
