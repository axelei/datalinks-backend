package net.krusher.datalinks.controller;

import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.page.GetPageCommand;
import net.krusher.datalinks.page.GetPageCommandHandler;
import net.krusher.datalinks.page.PostPageCommand;
import net.krusher.datalinks.page.PostPageCommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/page")
public class PageController {

    private final GetPageCommandHandler getPageCommandHandler;
    private final PostPageCommandHandler postPageCommandHandler;

    @Autowired
    public PageController(GetPageCommandHandler getPageCommandHandler, PostPageCommandHandler postPageCommandHandler) {
        this.getPageCommandHandler = getPageCommandHandler;
        this.postPageCommandHandler = postPageCommandHandler;
    }

    @GetMapping("{title}")
    ResponseEntity<Page> get(@PathVariable("title") String title, @RequestHeader(value = "user-token", required = false) String userToken) throws InterruptedException {
        Thread.sleep(200);
        return getPageCommandHandler.handler(GetPageCommand.builder()
                        .title(title)
                        .userToken(userToken)
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("{title}")
    void post(@PathVariable("title") String title, @RequestBody String content, @RequestHeader(value = "user-token", required = false) String userToken) throws InterruptedException {
        Thread.sleep(1000);
        postPageCommandHandler.handler(PostPageCommand.builder()
                .title(title)
                .content(content)
                .userToken(userToken)
                .build());
    }
}
