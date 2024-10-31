package net.krusher.datalinks.user;

import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.User;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LoginCommandHandler {

    private final UserService userService;

    @Autowired
    public LoginCommandHandler(UserService userService) {
        this.userService = userService;
    }

    public Optional<LoginToken> handler(LoginCommand loginCommand) {

        String hash = DigestUtils.sha256Hex(loginCommand.getPassword());
        Optional<User> user = userService.getByUsernameAndHash(loginCommand.getUsername(), hash);
        if (user.isPresent()) {
            LoginToken loginToken = LoginToken.builder()
                    .userId(user.get().getId())
                    .token(UUID.randomUUID())
                    .build();
            return Optional.of(loginToken);
        } else {
            return Optional.empty();
        }
    }
}
