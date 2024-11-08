package net.krusher.datalinks.handler.user;

import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetUserByLoginTokenCommandHandler {

    private final UserService userService;
    private final LoginTokenService loginTokenService;

    @Autowired
    public GetUserByLoginTokenCommandHandler(UserService userService, LoginTokenService loginTokenService) {
        this.userService = userService;
        this.loginTokenService = loginTokenService;
    }

    public Optional<User> handler(GetUserByLoginTokenCommand getUserByLoginTokenCommand) {
        return loginTokenService.getById(getUserByLoginTokenCommand.getLoginToken())
                .map(LoginToken::getUserId)
                .flatMap(userService::getById);
    }
}
