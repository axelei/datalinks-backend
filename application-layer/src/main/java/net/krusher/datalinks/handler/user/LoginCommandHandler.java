package net.krusher.datalinks.handler.user;

import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginCommandHandler {

    private final UserService userService;
    private final LoginTokenService loginTokenService;

    @Autowired
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
