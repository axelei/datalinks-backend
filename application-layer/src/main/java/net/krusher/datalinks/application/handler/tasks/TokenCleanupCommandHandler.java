package net.krusher.datalinks.application.handler.tasks;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.ResetTokenService;

@ApplicationScoped
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class TokenCleanupCommandHandler {

    private final LoginTokenService loginTokenService;
    private final ResetTokenService resetTokenService;


    @Transactional
    public void handler() {
        loginTokenService.deleteExpired();
        resetTokenService.deleteExpired();
    }

}
