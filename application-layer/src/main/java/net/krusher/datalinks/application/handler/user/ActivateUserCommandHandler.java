package net.krusher.datalinks.application.handler.user;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.user.User;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class ActivateUserCommandHandler {

    private final UserService userService;


    @Transactional
    public void handler(UUID activationToken) {
        Optional<User> user = userService.getByActivationToken(activationToken);
        if (user.isPresent()) {
            user.get().setActivationToken(null);
            userService.save(user.get());
        } else {
            throw new EngineException(ErrorType.RESET_REQUEST_NOT_FOUND, "User activation token not found");
        }
    }
}
