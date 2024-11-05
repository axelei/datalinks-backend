package net.krusher.datalinks.handler.page;

import com.github.slugify.Slugify;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetPageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    private final Slugify slugify = Slugify.builder().build();

    @Autowired
    public GetPageCommandHandler(PageService pageService, UserHelper userHelper) {
        this.pageService = pageService;
        this.userHelper = userHelper;
    }

    public Optional<Page> handler(GetPageCommand getPageCommand) {
        String slug = slugify.slugify(getPageCommand.getTitle());
        Optional<Page> page = pageService.findBySlug(slug);
        if (page.isPresent() && !userHelper.userCanRead(page.get(), getPageCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't read this page");
        }
        return pageService.findBySlug(slug);
    }
}