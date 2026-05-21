package net.krusher.datalinks.application.handler.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.model.user.LoginToken;
import net.krusher.datalinks.domain.model.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;
import java.security.MessageDigest;
import java.util.UUID;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class LoginCommandHandler {

    private final UserService userService;
    private final LoginTokenService loginTokenService;


    @Transactional
    public Optional<LoginToken> handler(LoginCommand loginCommand) {
        Optional<User> user = userService.getByUsername(loginCommand.getUsername());
        return user.filter(u -> u.getActivationToken() == null)
                .filter(u -> isPasswordValid(u, loginCommand.getPassword()))
                .map(u -> {
                    LoginToken loginToken = LoginToken.builder()
                            .userId(u.getId())
                            .loginToken(UUID.randomUUID())
                            .build();
                    loginTokenService.saveToken(loginToken);
                    return loginToken;
                });
    }

    private boolean isPasswordValid(User user, String rawPassword) {
        String stored = user.getPassword();
        if (stored.startsWith("$2a$") || stored.startsWith("$2b$") || stored.startsWith("$2y$")) {
            return BCrypt.checkpw(rawPassword, stored);
        }
        String shaHash = DigestUtils.sha256Hex(user.getSalt() + rawPassword);
        return MessageDigest.isEqual(stored.getBytes(), shaHash.getBytes());
    }
}
