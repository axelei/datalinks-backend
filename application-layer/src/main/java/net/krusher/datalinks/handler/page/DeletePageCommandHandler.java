package net.krusher.datalinks.handler.page;

import com.github.slugify.Slugify;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class DeletePageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    private final Slugify slugify = Slugify.builder().build();

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
        Optional<Page> page = pageService.findBySlug(slugify.slugify(deletePageCommand.getTitle()));
        if (page.isEmpty()) {
            throw new EngineException(ErrorType.PAGE_NOT_FOUND, "Page not found");
        }
        pageService.delete(page.get().getId());
    }
}
