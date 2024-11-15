package net.krusher.datalinks.handler.page;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static net.krusher.datalinks.handler.common.SlugifyProvider.SLUGIFY;

@Service
public class PostPageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    @Autowired
    public PostPageCommandHandler(PageService pageService, UserHelper userHelper) {
        this.pageService = pageService;
        this.userHelper = userHelper;
    }

    @Transactional
    public void handler(PostPageCommand postPageCommand) {
        pageService.findBySlug(SLUGIFY.slugify(postPageCommand.getTitle()))
                .ifPresentOrElse(page -> updatePage(page, postPageCommand), () -> createPage(postPageCommand));
    }

    private void createPage(PostPageCommand postPageCommand) {
        if (!userHelper.userCanCreate(postPageCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't create a page");
        }

        Optional<User> user = userHelper.getUserFromLoginToken(postPageCommand.getLoginTokenId());
        Page page = Page.builder()
                .title(postPageCommand.getTitle())
                .content(postPageCommand.getContent())
                .slug(SLUGIFY.slugify(postPageCommand.getTitle()))
                .creatorId(user.map(User::getId).orElse(null))
                .build();
        pageService.save(page, user.orElse(null), postPageCommand.getIp());
    }

    private void updatePage(Page page, PostPageCommand postPageCommand) {
        if (!userHelper.userCanEdit(page, postPageCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't edit this page");
        }
        Optional<User> user = userHelper.getUserFromLoginToken(postPageCommand.getLoginTokenId());
        page.setSlug(SLUGIFY.slugify(postPageCommand.getTitle()));
        page.setContent(postPageCommand.getContent());
        pageService.save(page, user.orElse(null), postPageCommand.getIp());
    }
}
