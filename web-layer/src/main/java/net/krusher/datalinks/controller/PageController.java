package net.krusher.datalinks.controller;

import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.page.GetPageCommandHandler;
import net.krusher.datalinks.page.PostPageCommandHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/page")
public class PageController {

    @GetMapping("{name}")
    Page get(@PathVariable("name") String name) throws InterruptedException {
        Thread.sleep(1000);
        GetPageCommandHandler handler = new GetPageCommandHandler();
        return handler.handler(name);
    }

    @PostMapping("{name}")
    void post(@PathVariable("name") String name, @RequestBody String content) throws InterruptedException {
        PostPageCommandHandler handler = new PostPageCommandHandler();
        Thread.sleep(1000);
    }
}
