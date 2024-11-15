package net.krusher.datalinks.handler.page;

import com.github.slugify.Slugify;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.handler.common.SlugifyProvider;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static net.krusher.datalinks.handler.common.SlugifyProvider.SLUGIFY;

@Service
public class DeletePageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    @Autowired
    public DeletePageCommandHandler(PageService pageService, UserHelper userHelper) {
        this.pageService = pageService;
        this.userHelper = userHelper;
    }

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
