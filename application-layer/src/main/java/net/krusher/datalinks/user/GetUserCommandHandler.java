package net.krusher.datalinks.user;

import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetUserCommandHandler {

    private final UserService userService;

    @Autowired
    public GetUserCommandHandler(UserService userService) {
        this.userService = userService;
    }

    public Optional<User> handler(GetUserCommand getUserCommand) {
        return userService.getByUsername(getUserCommand.getUsername());
    }
}
