package net.krusher.datalinks.tasks;

import net.krusher.datalinks.handler.tasks.LinkerCommandHandler;
import net.krusher.datalinks.handler.tasks.UnlinkerCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LinkTasks {

    private final LinkerCommandHandler linkerCommandHandler;
    private final UnlinkerCommandHandler unlinkerCommandHandler;

    @Autowired
    public LinkTasks(LinkerCommandHandler linkerCommandHandler, UnlinkerCommandHandler unlinkerCommandHandler) {
        this.linkerCommandHandler = linkerCommandHandler;
        this.unlinkerCommandHandler = unlinkerCommandHandler;
    }

    @Async("linkExecutor")
    @Scheduled(cron = "0 0 5 * * *")
    public void linkerTask() {
        linkerCommandHandler.handler();
    }

    @Async("linkExecutor")
    @Scheduled(cron = "0 0 7 * * *")
    public void unlinkerTask() {
        unlinkerCommandHandler.handler();
    }

}
