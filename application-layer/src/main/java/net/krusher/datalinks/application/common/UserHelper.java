package net.krusher.datalinks.application.common;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.model.configlet.ConfigletKey;
import net.krusher.datalinks.domain.model.page.Page;
import net.krusher.datalinks.domain.model.page.PageShort;
import net.krusher.datalinks.domain.model.upload.Upload;
import net.krusher.datalinks.domain.model.user.User;
import net.krusher.datalinks.domain.model.user.UserLevel;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.UUID;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class UserHelper {

    private final UserService userService;
    private final LoginTokenService loginTokenService;
    private final ConfigService configService;


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

    private UserLevel getUserLevel(UUID loginTokenId) {
        return getUserFromLoginToken(loginTokenId).map(User::getLevel).orElse(UserLevel.GUEST);
    }

    private boolean hasPermission(ConfigletKey key, UUID loginTokenId, UserLevel resourceLevel) {
        UserLevel defaultLevel = UserLevel.valueOf(configService.getByKey(key).getValue());
        UserLevel userLevel = getUserLevel(loginTokenId);
        UserLevel needed = Optional.ofNullable(resourceLevel).orElse(defaultLevel);
        return needed.getLevel() <= userLevel.getLevel();
    }

    public boolean userCanRead(Page page, UUID loginTokenId) {
        return hasPermission(ConfigletKey.READ_LEVEL, loginTokenId, page.getReadBlock());
    }

    public boolean userCanRead(PageShort page, UUID loginTokenId) {
        return hasPermission(ConfigletKey.READ_LEVEL, loginTokenId, page.getReadBlock());
    }

    public boolean userCanEdit(Page page, UUID loginTokenId) {
        return hasPermission(ConfigletKey.EDIT_LEVEL, loginTokenId, page.getEditBlock());
    }

    public boolean userCanDelete(UUID loginTokenId) {
        return hasPermission(ConfigletKey.DELETE_LEVEL, loginTokenId, null);
    }

    public boolean userCanCreate(UUID loginTokenId) {
        return hasPermission(ConfigletKey.CREATE_LEVEL, loginTokenId, null);
    }

    public boolean userCanSeeFile(Upload upload, UUID loginTokenId) {
        return hasPermission(ConfigletKey.SEE_FILE_LEVEL, loginTokenId, upload.getReadBlock());
    }

    public boolean userCanUpload(UUID loginTokenId) {
        return hasPermission(ConfigletKey.UPLOAD_LEVEL, loginTokenId, null);
    }

    public boolean userCanUpdateUpload(Upload upload, UUID loginTokenId) {
        return hasPermission(ConfigletKey.UPDATE_UPLOAD_LEVEL, loginTokenId, upload.getEditBlock());
    }

    public boolean userCanDeleteUpload(UUID loginTokenId) {
        return hasPermission(ConfigletKey.DELETE_UPLOAD_LEVEL, loginTokenId, null);
    }

    public boolean isAdmin(UUID loginTokenId) {
        return getUserFromLoginToken(loginTokenId).filter(user -> UserLevel.ADMIN.equals(user.getLevel())).isPresent();
    }

}
