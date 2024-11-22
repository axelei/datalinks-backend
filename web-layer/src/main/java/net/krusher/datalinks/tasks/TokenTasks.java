package net.krusher.datalinks.tasks;

import net.krusher.datalinks.handler.tasks.TokenCleanupCommandHandler;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TokenTasks {

    private final TokenCleanupCommandHandler tokenCleanupCommandHandler;

    public TokenTasks(TokenCleanupCommandHandler tokenCleanupCommandHandler) {
        this.tokenCleanupCommandHandler = tokenCleanupCommandHandler;
    }

    @Scheduled(cron = "0 0 4 * * *")
    public void tokenCleanup() {
        tokenCleanupCommandHandler.handler();
    }
}
