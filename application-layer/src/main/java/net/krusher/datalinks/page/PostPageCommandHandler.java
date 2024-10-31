package net.krusher.datalinks.page;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.model.configlet.ConfigletKey;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;
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
        pageService.findByTitle(postPageCommand.getTitle())
                .ifPresentOrElse(page -> updatePage(page, postPageCommand), () -> createPage(postPageCommand));
    }

    private void createPage(PostPageCommand postPageCommand) {

        Optional<LoginToken> loginToken =  Optional.ofNullable(postPageCommand.getLoginTokenId()).flatMap(loginTokenService::getById);
        Optional<User> user = loginToken.flatMap(token -> userService.getById(token.getUserId()));

        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.CREATE_LEVEL).getValue());
        UserLevel userLevel = user.map(User::getLevel).orElse(UserLevel.GUEST);

        if (defaultBlock.getLevel() > userLevel.getLevel()) {
            throw new RuntimeException("User can't create a page");
        }

        Page page = Page.builder()
                .title(postPageCommand.getTitle())
                .content(postPageCommand.getContent())
                .build();
        pageService.save(page);
    }

    private void updatePage(Page page, PostPageCommand postPageCommand) {

        Optional<LoginToken> loginToken =  Optional.ofNullable(postPageCommand.getLoginTokenId()).flatMap(loginTokenService::getById);
        Optional<User> user = loginToken.flatMap(token -> userService.getById(token.getUserId()));

        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.DEFAULT_EDIT_LEVEL).getValue());

        UserLevel userLevel = user.map(User::getLevel).orElse(UserLevel.GUEST);
        UserLevel neededLevel = Optional.ofNullable(page.getEditBlock()).orElse(defaultBlock);

        if (neededLevel.getLevel() > userLevel.getLevel()) {
            throw new RuntimeException("User can't edit this page");
        }

        page.setContent(postPageCommand.getContent());
        pageService.save(page);
    }
}
