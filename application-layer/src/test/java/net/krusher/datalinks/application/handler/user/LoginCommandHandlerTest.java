package net.krusher.datalinks.application.handler.user;

import net.krusher.datalinks.domain.model.user.LoginToken;
import net.krusher.datalinks.domain.model.user.User;
import net.krusher.datalinks.domain.model.user.UserLevel;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;

class LoginCommandHandlerTest {

    @Test
    void successfulLoginReturnsToken() {
        String salt = BCrypt.gensalt(12);
        String hashedPassword = BCrypt.hashpw("pass", salt);
        User user = User.builder().username("u").salt(salt).password(hashedPassword).level(UserLevel.USER).id(UUID.randomUUID()).build();

        UserService userService = Mockito.mock(UserService.class);
        when(userService.getByUsername("u")).thenReturn(Optional.of(user));

        LoginTokenService loginTokenService = Mockito.mock(LoginTokenService.class);
        ArgumentCaptor<LoginToken> captor = ArgumentCaptor.forClass(LoginToken.class);
        doNothing().when(loginTokenService).saveToken(captor.capture());

        LoginCommandHandler handler = new LoginCommandHandler(userService, loginTokenService);
        Optional<LoginToken> res = handler.handler(LoginCommand.builder().username("u").password("pass").build());
        assertTrue(res.isPresent());
        verify(loginTokenService).saveToken(any());
        assertEquals(user.getId(), captor.getValue().getUserId());
    }

    @Test
    void successfulLoginWithLegacySha256Password() {
        String salt = "s";
        String hashedPassword = DigestUtils.sha256Hex(salt + "pass");
        User user = User.builder().username("u").salt(salt).password(hashedPassword).level(UserLevel.USER).id(UUID.randomUUID()).build();

        UserService userService = Mockito.mock(UserService.class);
        when(userService.getByUsername("u")).thenReturn(Optional.of(user));

        LoginTokenService loginTokenService = Mockito.mock(LoginTokenService.class);
        ArgumentCaptor<LoginToken> captor = ArgumentCaptor.forClass(LoginToken.class);
        doNothing().when(loginTokenService).saveToken(captor.capture());

        LoginCommandHandler handler = new LoginCommandHandler(userService, loginTokenService);
        Optional<LoginToken> res = handler.handler(LoginCommand.builder().username("u").password("pass").build());
        assertTrue(res.isPresent());
        verify(loginTokenService).saveToken(any());
    }

    @Test
    void failedLoginReturnsEmpty() {
        UserService userService = Mockito.mock(UserService.class);
        when(userService.getByUsername("u")).thenReturn(Optional.empty());

        LoginTokenService loginTokenService = Mockito.mock(LoginTokenService.class);

        LoginCommandHandler handler = new LoginCommandHandler(userService, loginTokenService);
        Optional<LoginToken> res = handler.handler(LoginCommand.builder().username("u").password("pass").build());
        assertTrue(res.isEmpty());
        verify(loginTokenService, never()).saveToken(any());
    }
}
