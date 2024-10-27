package net.krusher.datalinks.controller;

import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.page.GetPageCommandHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PageController {

    @GetMapping("/page/{name}")
    Page get(@PathVariable("name") String name) throws InterruptedException {
        Thread.sleep(1000);
        GetPageCommandHandler handler = new GetPageCommandHandler();
        return handler.handler(name);
    }
}
