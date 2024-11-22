package net.krusher.datalinks.tasks;

import net.krusher.datalinks.handler.tasks.LinkerCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class LinkTasks {

    private final LinkerCommandHandler linkerCommandHandler;

    @Autowired
    public LinkTasks(LinkerCommandHandler linkerCommandHandler) {
        this.linkerCommandHandler = linkerCommandHandler;
    }

    @Scheduled(cron = "0 0 5 * * *")
    public void linkerTask() {
        linkerCommandHandler.handler();
    }
}
