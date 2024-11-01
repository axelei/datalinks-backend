package net.krusher.datalinks.common;

import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.model.configlet.ConfigletKey;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserHelper {

    private final UserService userService;
    private final LoginTokenService loginTokenService;
    private final ConfigService configService;

    @Autowired
    public UserHelper(UserService userService, LoginTokenService loginTokenService, ConfigService configService) {
        this.userService = userService;
        this.loginTokenService = loginTokenService;
        this.configService = configService;
    }

    public boolean userCanRead(Page page, UUID loginTokenId) {
        Optional<LoginToken> loginToken =  Optional.ofNullable(loginTokenId).flatMap(loginTokenService::getById);
        Optional<User> user = loginToken.flatMap(token -> userService.getById(token.getUserId()));

        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.READ_LEVEL).getValue());

        UserLevel userLevel = user.map(User::getLevel).orElse(UserLevel.GUEST);
        UserLevel neededLevel = Optional.ofNullable(page.getReadBlock()).orElse(defaultBlock);

        return neededLevel.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanEdit(Page page, UUID loginTokenId) {
        Optional<LoginToken> loginToken =  Optional.ofNullable(loginTokenId).flatMap(loginTokenService::getById);
        Optional<User> user = loginToken.flatMap(token -> userService.getById(token.getUserId()));

        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.EDIT_LEVEL).getValue());

        UserLevel userLevel = user.map(User::getLevel).orElse(UserLevel.GUEST);
        UserLevel neededLevel = Optional.ofNullable(page.getEditBlock()).orElse(defaultBlock);

        return neededLevel.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanCreate(@Nullable UUID loginTokenId) {
        Optional<LoginToken> loginToken =  Optional.ofNullable(loginTokenId).flatMap(loginTokenService::getById);
        Optional<User> user = loginToken.flatMap(token -> userService.getById(token.getUserId()));

        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.CREATE_LEVEL).getValue());
        UserLevel userLevel = user.map(User::getLevel).orElse(UserLevel.GUEST);

        return defaultBlock.getLevel() <= userLevel.getLevel();
    }

}
