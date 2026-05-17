package net.krusher.datalinks.tasks;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.handler.tasks.LinkerCommandHandler;
import net.krusher.datalinks.handler.tasks.UnlinkerCommandHandler;

@ApplicationScoped
public class LinkTasks {

    private final LinkerCommandHandler linkerCommandHandler;
    private final UnlinkerCommandHandler unlinkerCommandHandler;

    @Inject
    public LinkTasks(LinkerCommandHandler linkerCommandHandler, UnlinkerCommandHandler unlinkerCommandHandler) {
        this.linkerCommandHandler = linkerCommandHandler;
        this.unlinkerCommandHandler = unlinkerCommandHandler;
    }

    @Scheduled(cron = "0 0 5 * * ?", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void linkerTask() {
        linkerCommandHandler.handler();
    }

    @Scheduled(cron = "0 0 7 * * ?", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void unlinkerTask() {
        unlinkerCommandHandler.handler();
    }

}
