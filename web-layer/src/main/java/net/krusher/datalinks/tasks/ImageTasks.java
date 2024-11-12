package net.krusher.datalinks.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ImageTasks {

    @Scheduled(cron = "0 0 6 * * *")
    public void markUnusedImages() {
        System.out.println("Marking unused images");

    }
}
