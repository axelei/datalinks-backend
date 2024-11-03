package net.krusher.datalinks.controller;

import com.github.slugify.Slugify;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.handler.page.GetPageCommand;
import net.krusher.datalinks.handler.page.GetPageCommandHandler;
import net.krusher.datalinks.handler.page.PostPageCommand;
import net.krusher.datalinks.handler.page.PostPageCommandHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

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
    ResponseEntity<Page> get(@PathVariable("title") String title, @RequestHeader(value = "login-token", required = false) String userToken) {
        return getPageCommandHandler.handler(GetPageCommand.builder()
                        .title(title)
                        .loginTokenId(StringUtils.isEmpty(userToken) ? null : UUID.fromString(userToken))
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{title}")
    void delete(@PathVariable("title") String title, @RequestHeader(value = "login-token", required = false) String userToken) {

    }

    @GetMapping("{title}/block")
    void block(@PathVariable("title") String title, @RequestHeader(value = "login-token", required = false) String userToken) {

    }

   @PutMapping("{title}")
    void put(@PathVariable("title") String title, @RequestBody String content, @RequestHeader(value = "login-token", required = false) String userToken) {
        postPageCommandHandler.handler(PostPageCommand.builder()
                .title(title)
                .content(content)
                .loginTokenId(StringUtils.isEmpty(userToken) ? null : UUID.fromString(userToken))
                .build());
    }
}
