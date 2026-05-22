package net.krusher.datalinks.web.tasks;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import net.krusher.datalinks.application.handler.tasks.LinkerCommandHandler;
import net.krusher.datalinks.application.handler.tasks.UnlinkerCommandHandler;

@ApplicationScoped
public class LinkTasks {

    private final LinkerCommandHandler linkerCommandHandler;
    private final UnlinkerCommandHandler unlinkerCommandHandler;

    @Inject
    public LinkTasks(LinkerCommandHandler linkerCommandHandler, UnlinkerCommandHandler unlinkerCommandHandler) {
        this.linkerCommandHandler = linkerCommandHandler;
        this.unlinkerCommandHandler = unlinkerCommandHandler;
    }

    @ActivateRequestContext
    @Scheduled(cron = "0 0 5 * * ?", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void linkerTask() {
        linkerCommandHandler.handler();
    }

    @ActivateRequestContext
    @Scheduled(cron = "0 0 7 * * ?", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    public void unlinkerTask() {
        unlinkerCommandHandler.handler();
    }

}
