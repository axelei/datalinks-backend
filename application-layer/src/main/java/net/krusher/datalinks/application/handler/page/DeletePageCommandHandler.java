package net.krusher.datalinks.application.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.page.Page;

import java.util.Optional;

import static net.krusher.datalinks.application.handler.common.SlugifyProvider.SLUGIFY;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class DeletePageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;


    @Transactional
    public void handler(DeletePageCommand deletePageCommand) {
        if (!userHelper.userCanDelete(deletePageCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't delete page");
        }
        Optional<Page> page = pageService.findBySlug(SLUGIFY.slugify(deletePageCommand.getTitle()));
        if (page.isEmpty()) {
            throw new EngineException(ErrorType.PAGE_NOT_FOUND, "Page not found");
        }
        pageService.delete(page.get().getId());
    }
}
