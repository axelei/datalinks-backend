package net.krusher.datalinks.controller;

import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.page.GetPageCommandHandler;
import net.krusher.datalinks.page.PostPageCommandHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/page")
public class PageController {

    @GetMapping("{name}")
    ResponseEntity<Page> get(@PathVariable("name") String name, @RequestHeader(value = "user-token", required = false) String userToken) throws InterruptedException {
        Thread.sleep(200);
        GetPageCommandHandler handler = new GetPageCommandHandler();
        return ResponseEntity.ok(handler.handler(name));
    }

    @PostMapping("{name}")
    void post(@PathVariable("name") String name, @RequestBody String content, @RequestHeader(value = "user-token", required = false) String userToken) throws InterruptedException {
        PostPageCommandHandler handler = new PostPageCommandHandler();
        Thread.sleep(1000);
    }
}
