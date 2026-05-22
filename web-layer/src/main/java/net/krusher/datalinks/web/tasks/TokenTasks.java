package net.krusher.datalinks.web.tasks;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import net.krusher.datalinks.application.handler.tasks.TokenCleanupCommandHandler;

@ApplicationScoped
public class TokenTasks {

    private final TokenCleanupCommandHandler tokenCleanupCommandHandler;

    @Inject
    public TokenTasks(TokenCleanupCommandHandler tokenCleanupCommandHandler) {
        this.tokenCleanupCommandHandler = tokenCleanupCommandHandler;
    }

    @ActivateRequestContext
    @Scheduled(cron = "0 0 4 * * ?")
    public void tokenCleanup() {
        tokenCleanupCommandHandler.handler();
    }
}
