package net.krusher.datalinks.application.common;

import net.krusher.datalinks.domain.model.configlet.Configlet;
import net.krusher.datalinks.domain.model.configlet.ConfigletKey;
import net.krusher.datalinks.domain.model.page.Page;
import net.krusher.datalinks.domain.model.page.PageShort;
import net.krusher.datalinks.domain.model.upload.Upload;
import net.krusher.datalinks.domain.model.user.LoginToken;
import net.krusher.datalinks.domain.model.user.User;
import net.krusher.datalinks.domain.model.user.UserLevel;
import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserHelperTest {

    @Test
    void sanitizeTrimsFields() {
        var us = new User();
        us.setName("  Bob  ");
        us.setEmail("  a@b.com  ");
        us.setUsername("  bob  ");

        var helper = new UserHelper(null, null, null);
        helper.sanitize(us);
        assertEquals("Bob", us.getName());
        assertEquals("a@b.com", us.getEmail());
        assertEquals("bob", us.getUsername());
    }

    @Test
    void permissionChecksUseConfigDefaultsAndTokenUser() {
        var user = User.builder().username("a").level(UserLevel.ADMIN).id(UUID.randomUUID()).build();
        var loginToken = LoginToken.builder().userId(user.getId()).loginToken(UUID.randomUUID()).build();

        LoginTokenService loginTokenService = Mockito.mock(LoginTokenService.class);
        when(loginTokenService.getById(any(UUID.class))).thenReturn(Optional.of(loginToken));

        UserService userService = Mockito.mock(UserService.class);
        when(userService.getById(any(UUID.class))).thenReturn(Optional.of(user));

        ConfigService configService = Mockito.mock(ConfigService.class);
        when(configService.getByKey(any())).thenAnswer(invocation -> Configlet.of(invocation.getArgument(0), ((ConfigletKey)invocation.getArgument(0)).getDefaultValue()));

        var helper = new UserHelper(userService, loginTokenService, configService);

        Page p = Page.builder().readBlock(null).editBlock(null).build();
        PageShort ps = PageShort.builder().readBlock(null).editBlock(null).build();

        assertTrue(helper.userCanRead(p, loginToken.getLoginToken()));
        assertTrue(helper.userCanRead(ps, loginToken.getLoginToken()));
        assertTrue(helper.userCanEdit(p, loginToken.getLoginToken()));
        assertTrue(helper.userCanCreate(loginToken.getLoginToken()));
        assertTrue(helper.userCanDelete(loginToken.getLoginToken()));
        assertTrue(helper.userCanUpload(loginToken.getLoginToken()));
        assertTrue(helper.userCanDeleteUpload(loginToken.getLoginToken()));
        assertTrue(helper.userCanUpdateUpload(Upload.builder().editBlock(null).build(), loginToken.getLoginToken()));
        assertTrue(helper.userCanSeeFile(Upload.builder().readBlock(null).build(), loginToken.getLoginToken()));
        assertTrue(helper.isAdmin(loginToken.getLoginToken()));
    }
}
