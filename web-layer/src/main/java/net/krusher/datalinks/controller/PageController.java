package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.krusher.datalinks.handler.page.GetPageCommand;
import net.krusher.datalinks.handler.page.GetPageCommandHandler;
import net.krusher.datalinks.handler.page.NewPagesCommand;
import net.krusher.datalinks.handler.page.NewPagesCommandHandler;
import net.krusher.datalinks.handler.page.PostPageCommand;
import net.krusher.datalinks.handler.page.PostPageCommandHandler;
import net.krusher.datalinks.model.NewPagesModel;
import net.krusher.datalinks.model.PostPageModel;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static net.krusher.datalinks.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.common.ControllerUtil.toLoginToken;

@RestController
@RequestMapping("/page")
public class PageController {

    private final GetPageCommandHandler getPageCommandHandler;
    private final PostPageCommandHandler postPageCommandHandler;
    private final NewPagesCommandHandler newPagesCommandHandler;
    private final ObjectMapper objectMapper;

    @Autowired
    public PageController(GetPageCommandHandler getPageCommandHandler,
                          PostPageCommandHandler postPageCommandHandler,
                          NewPagesCommandHandler newPagesCommandHandler,
                          ObjectMapper objectMapper) {
        this.getPageCommandHandler = getPageCommandHandler;
        this.postPageCommandHandler = postPageCommandHandler;
        this.newPagesCommandHandler = newPagesCommandHandler;
        this.objectMapper = objectMapper;
    }

    @GetMapping("{title}")
    public ResponseEntity<Page> get(@PathVariable("title") String title, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {
        return getPageCommandHandler.handler(GetPageCommand.builder()
                        .title(title)
                        .loginTokenId(toLoginToken(userToken))
                        .build())
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("{title}")
    public void delete(@PathVariable("title") String title, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {

    }

    @GetMapping("{title}/block/{block}")
    public void block(@PathVariable("title") String title, @PathVariable("block") String block, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {

    }

    @PutMapping("{title}")
    public void put(@PathVariable("title") String title, @RequestBody String body, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) throws JsonProcessingException {
        PostPageModel postPageModel = objectMapper.readValue(body, PostPageModel.class);
        postPageCommandHandler.handler(PostPageCommand.builder()
                .title(title)
                .content(postPageModel.getContent())
                .categories(postPageModel.getCategories())
                .loginTokenId(toLoginToken(userToken))
                .build());
    }

    @PostMapping("newPages")
    public ResponseEntity<List<Page>> newPages(@RequestBody String body) throws JsonProcessingException {
        NewPagesModel newPagesModel = objectMapper.readValue(body, NewPagesModel.class);
        List<Page> pages = newPagesCommandHandler.handler(NewPagesCommand.builder()
                .page(newPagesModel.getPage())
                .pageSize(newPagesModel.getPageSize())
                .build());
        return ResponseEntity.ok(pages);
    }
}
