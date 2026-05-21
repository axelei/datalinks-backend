package net.krusher.datalinks.application.handler.user;

import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.application.mapper.SignupMapper;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.model.configlet.Configlet;
import net.krusher.datalinks.domain.model.configlet.ConfigletKey;
import net.krusher.datalinks.domain.model.user.User;
import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.engineering.model.domain.email.EmailService;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SignupCommandHandlerTest {

    @Test
    void successfulSignupSavesUserAndSendsEmail() {
        UserService userService = Mockito.mock(UserService.class);
        when(userService.getByUsername(anyString())).thenReturn(Optional.empty());

        EmailService emailService = Mockito.mock(EmailService.class);

        SignupMapper mapper = Mockito.mock(SignupMapper.class);
        when(mapper.toModel(any(SignupCommand.class))).thenAnswer(invocation -> {
            SignupCommand cmd = invocation.getArgument(0);
            return User.builder().username(cmd.getUsername()).email(cmd.getEmail()).name(cmd.getName()).language(cmd.getLanguage()).build();
        });

        LoginTokenService loginTokenService = Mockito.mock(LoginTokenService.class);
        when(loginTokenService.getById(any(UUID.class))).thenReturn(Optional.empty());

        ConfigService configService = Mockito.mock(ConfigService.class);
        when(configService.getByKey(any())).thenAnswer(invocation -> Configlet.of(invocation.getArgument(0), ((ConfigletKey)invocation.getArgument(0)).getDefaultValue()));

        UserHelper helper = new UserHelper(userService, loginTokenService, configService);

        SignupCommandHandler handler = new SignupCommandHandler(helper, userService, mapper, emailService);

        SignupCommand cmd = SignupCommand.builder().username("bob").password("password1").email("a@b.com").name("Bob").language("en").build();

        assertDoesNotThrow(() -> handler.handle(cmd));
        verify(emailService).sendSignupMessage(eq("a@b.com"), any(Map.class), eq("en"));
    }

    @Test
    void duplicateUserThrows() {
        UserService userService = Mockito.mock(UserService.class);
        when(userService.getByUsername(anyString())).thenReturn(Optional.of(User.builder().username("bob").build()));

        LoginTokenService loginTokenService = Mockito.mock(LoginTokenService.class);
        ConfigService configService = Mockito.mock(ConfigService.class);
        when(configService.getByKey(any())).thenAnswer(invocation -> Configlet.of(invocation.getArgument(0), ((ConfigletKey)invocation.getArgument(0)).getDefaultValue()));

        UserHelper helper = new UserHelper(userService, loginTokenService, configService);

        SignupMapper mapper = cmd -> User.builder().username(cmd.getUsername()).email(cmd.getEmail()).name(cmd.getName()).build();
        EmailService emailService = Mockito.mock(EmailService.class);

        SignupCommandHandler handler = new SignupCommandHandler(helper, userService, mapper, emailService);

        SignupCommand cmd = SignupCommand.builder().username("bob").password("password1").email("a@b.com").name("Bob").language("en").build();

        EngineException ex = assertThrows(EngineException.class, () -> handler.handle(cmd));
        assertTrue(ex.getMessage().contains("User already exists"));
    }
}
