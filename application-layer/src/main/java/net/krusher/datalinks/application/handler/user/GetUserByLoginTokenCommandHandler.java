package net.krusher.datalinks.application.handler.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.model.user.LoginToken;
import net.krusher.datalinks.domain.model.user.User;

import java.util.Optional;

@ApplicationScoped
public class GetUserByLoginTokenCommandHandler {

    private final UserService userService;
    private final LoginTokenService loginTokenService;

    @Inject
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
