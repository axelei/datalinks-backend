package net.krusher.datalinks.handler.user;

import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class ActivateUserCommandHandler {

    private final UserService userService;

    @Autowired
    public ActivateUserCommandHandler(UserService userService) {
        this.userService = userService;
    }

    @Transactional
    public void handler(UUID activationToken) {
        Optional<User> user = userService.getByActivationToken(activationToken);
        if (user.isPresent()) {
            user.get().setActivationToken(null);
            userService.save(user.get());
        } else {
            throw new EngineException(ErrorType.USER_NOT_FOUND, "User activation token not found");
        }
    }
}
