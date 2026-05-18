package net.krusher.datalinks.application.handler.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.model.user.User;

import java.util.Optional;

@ApplicationScoped
public class GetUserCommandHandler {

    private final UserService userService;

    @Inject
    public GetUserCommandHandler(UserService userService) {
        this.userService = userService;
    }

    public Optional<User> handler(GetUserCommand getUserCommand) {
        return userService.getByUsername(getUserCommand.getUsername());
    }
}
