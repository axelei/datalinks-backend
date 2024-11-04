package net.krusher.datalinks.handler.page;

import com.github.slugify.Slugify;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PostPageCommandHandler {

    private final PageService pageService;
    private final UserService userService;
    private final LoginTokenService loginTokenService;
    private final UserHelper userHelper;
    private final ConfigService configService;

    private final Slugify slugify = Slugify.builder().build();

    @Autowired
    public PostPageCommandHandler(PageService pageService, UserService userService, LoginTokenService loginTokenService, UserHelper userHelper, ConfigService configService) {
        this.pageService = pageService;
        this.userService = userService;
        this.loginTokenService = loginTokenService;
        this.userHelper = userHelper;
        this.configService = configService;
    }

    @Transactional
    public void handler(PostPageCommand postPageCommand) {
        pageService.findBySlug(slugify.slugify(postPageCommand.getTitle()))
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
                .slug(slugify.slugify(postPageCommand.getTitle()))
                .creatorId(user.map(User::getId).orElse(null))
                .build();
        pageService.save(page);
    }

    private void updatePage(Page page, PostPageCommand postPageCommand) {
        if (!userHelper.userCanEdit(page, postPageCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't edit this page");
        }

        page.setSlug(slugify.slugify(postPageCommand.getTitle()));
        page.setContent(postPageCommand.getContent());
        pageService.save(page);
    }
}
