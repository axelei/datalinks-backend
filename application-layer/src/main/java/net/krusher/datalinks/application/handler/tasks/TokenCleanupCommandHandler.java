package net.krusher.datalinks.application.handler.tasks;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.ResetTokenService;

@ApplicationScoped
public class TokenCleanupCommandHandler {

    private final LoginTokenService loginTokenService;
    private final ResetTokenService resetTokenService;

    @Inject
    public TokenCleanupCommandHandler(LoginTokenService loginTokenService,
                                      ResetTokenService resetTokenService) {
        this.loginTokenService = loginTokenService;
        this.resetTokenService = resetTokenService;
    }

    @Transactional
    public void handler() {
        loginTokenService.deleteExpired();
        resetTokenService.deleteExpired();
    }

}
