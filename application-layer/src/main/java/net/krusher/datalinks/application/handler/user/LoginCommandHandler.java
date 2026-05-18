package net.krusher.datalinks.application.handler.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.model.user.LoginToken;
import net.krusher.datalinks.domain.model.user.User;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class LoginCommandHandler {

    private final UserService userService;
    private final LoginTokenService loginTokenService;

    @Inject
    public LoginCommandHandler(UserService userService, LoginTokenService loginTokenService) {
        this.userService = userService;
        this.loginTokenService = loginTokenService;
    }

    @Transactional
    public Optional<LoginToken> handler(LoginCommand loginCommand) {
        Optional<User> user = userService.getByUsername(loginCommand.getUsername());
        if (user.isPresent() && user.get().getPassword().equals(DigestUtils.sha256Hex(user.get().getSalt() + loginCommand.getPassword())) && Objects.isNull(user.get().getActivationToken())) {
            LoginToken loginToken = LoginToken.builder()
                    .userId(user.get().getId())
                    .loginToken(UUID.randomUUID())
                    .build();
            loginTokenService.saveToken(loginToken);
            return Optional.of(loginToken);
        }
        return Optional.empty();
    }
}
