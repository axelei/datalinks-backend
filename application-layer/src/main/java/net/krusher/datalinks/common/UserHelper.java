package net.krusher.datalinks.common;

import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.model.configlet.ConfigletKey;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.upload.Upload;
import net.krusher.datalinks.model.user.User;
import net.krusher.datalinks.model.user.UserLevel;
import org.apache.commons.lang3.StringUtils;
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

    public Optional<User> getUserFromLoginToken(UUID loginTokenId) {
        return Optional.ofNullable(loginTokenId)
                .flatMap(loginTokenService::getById)
                .flatMap(token -> userService.getById(token.getUserId()));
    }

    public void sanitize(User user) {
        user.setName(StringUtils.trim(user.getName()));
        user.setEmail(StringUtils.trim(user.getEmail()));
        user.setUsername(StringUtils.trim(user.getUsername()));
    }

    private Optional<User> getUserFromToken(UUID loginTokenId) {
        return Optional.ofNullable(loginTokenId)
                .flatMap(loginTokenService::getById)
                .flatMap(token -> userService.getById(token.getUserId()));
    }

    public boolean userCanRead(Page page, UUID loginTokenId) {
        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.READ_LEVEL).getValue());
        UserLevel userLevel = getUserFromToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
        UserLevel neededLevel = Optional.ofNullable(page.getReadBlock()).orElse(defaultBlock);
        return neededLevel.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanEdit(Page page, UUID loginTokenId) {
        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.EDIT_LEVEL).getValue());
        UserLevel userLevel = getUserFromToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
        UserLevel neededLevel = Optional.ofNullable(page.getEditBlock()).orElse(defaultBlock);
        return neededLevel.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanDelete(UUID loginTokenId) {
        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.DELETE_LEVEL).getValue());
        UserLevel userLevel = getUserFromToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
        return defaultBlock.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanCreate(@Nullable UUID loginTokenId) {
        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.CREATE_LEVEL).getValue());
        UserLevel userLevel = getUserFromToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
        return defaultBlock.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanSeeFile(Upload upload, @Nullable UUID loginTokenId) {
        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.SEE_FILE_LEVEL).getValue());
        UserLevel userLevel = getUserFromToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
        UserLevel neededLevel = Optional.ofNullable(upload.getReadBlock()).orElse(defaultBlock);
        return neededLevel.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanUpload(@Nullable UUID loginTokenId) {
        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.UPLOAD_LEVEL).getValue());
        UserLevel userLevel = getUserFromToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
        return defaultBlock.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanUpdateUpload(Upload upload, @Nullable UUID loginTokenId) {
        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.UPDATE_UPLOAD_LEVEL).getValue());
        UserLevel userLevel = getUserFromToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
        UserLevel neededLevel = Optional.ofNullable(upload.getEditBlock()).orElse(defaultBlock);
        return neededLevel.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanDeleteUpload(@Nullable UUID loginTokenId) {
        UserLevel defaultBlock = UserLevel.valueOf(configService.getByKey(ConfigletKey.DELETE_UPLOAD_LEVEL).getValue());
        UserLevel userLevel = getUserFromToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
        return defaultBlock.getLevel() <= userLevel.getLevel();
    }

    public boolean isAdmin(UUID loginTokenId) {
        return getUserFromToken(loginTokenId).filter(user -> UserLevel.ADMIN.equals(user.getLevel())).isPresent();
    }

}
