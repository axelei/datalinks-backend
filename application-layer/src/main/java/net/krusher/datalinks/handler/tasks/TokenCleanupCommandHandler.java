package net.krusher.datalinks.handler.tasks;

import net.krusher.datalinks.engineering.model.domain.user.LoginTokenService;
import net.krusher.datalinks.engineering.model.domain.user.ResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenCleanupCommandHandler {

    private final LoginTokenService loginTokenService;
    private final ResetTokenService resetTokenService;

    @Autowired
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
