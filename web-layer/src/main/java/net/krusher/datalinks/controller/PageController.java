package net.krusher.datalinks.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.handler.page.DeletePageCommand;
import net.krusher.datalinks.handler.page.DeletePageCommandHandler;
import net.krusher.datalinks.handler.page.GetContributionsCommandHandler;
import net.krusher.datalinks.handler.page.GetEditCommandHandler;
import net.krusher.datalinks.handler.page.GetPageCommand;
import net.krusher.datalinks.handler.page.GetPageCommandHandler;
import net.krusher.datalinks.handler.page.GetRandomPageCommandHandler;
import net.krusher.datalinks.handler.page.NewPagesCommandHandler;
import net.krusher.datalinks.handler.page.PageEditsCommandHandler;
import net.krusher.datalinks.handler.page.PostPageCommand;
import net.krusher.datalinks.handler.page.PostPageCommandHandler;
import net.krusher.datalinks.handler.page.RecentChangesCommandHandler;
import net.krusher.datalinks.handler.page.SearchCommandHandler;
import net.krusher.datalinks.handler.page.TitleSearchCommandHandler;
import net.krusher.datalinks.model.PaginationModel;
import net.krusher.datalinks.model.PostPageModel;
import net.krusher.datalinks.model.page.Edit;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.page.PageShort;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

import static net.krusher.datalinks.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.common.ControllerUtil.toLoginToken;

@RestController
@RequestMapping("/page")
public class PageController {

    private final GetPageCommandHandler getPageCommandHandler;
    private final PostPageCommandHandler postPageCommandHandler;
    private final NewPagesCommandHandler newPagesCommandHandler;
    private final RecentChangesCommandHandler recentChangesCommandHandler;
    private final GetRandomPageCommandHandler getRandomPageCommandHandler;
    private final TitleSearchCommandHandler titleSearchCommandHandler;
    private final SearchCommandHandler searchCommandHandler;
    private final DeletePageCommandHandler deletePageCommandHandler;
    private final GetContributionsCommandHandler getContributionsCommandHandler;
    private final PageEditsCommandHandler pageEditsCommandHandler;
    private final GetEditCommandHandler getEditCommandHandler;
    private final ObjectMapper objectMapper;

    @Autowired
    public PageController(GetPageCommandHandler getPageCommandHandler,
                          PostPageCommandHandler postPageCommandHandler,
                          NewPagesCommandHandler newPagesCommandHandler,
                          RecentChangesCommandHandler recentChangesCommandHandler,
                          GetRandomPageCommandHandler getRandomPageCommandHandler,
                          TitleSearchCommandHandler titleSearchCommandHandler,
                          SearchCommandHandler searchCommandHandler,
                          DeletePageCommandHandler deletePageCommandHandler,
                          PageEditsCommandHandler pageEditsCommandHandler,
                          GetContributionsCommandHandler getContributionsCommandHandler,
                            GetEditCommandHandler getEditCommandHandler,
                          ObjectMapper objectMapper) {
        this.getPageCommandHandler = getPageCommandHandler;
        this.postPageCommandHandler = postPageCommandHandler;
        this.newPagesCommandHandler = newPagesCommandHandler;
        this.recentChangesCommandHandler = recentChangesCommandHandler;
        this.getRandomPageCommandHandler = getRandomPageCommandHandler;
        this.titleSearchCommandHandler = titleSearchCommandHandler;
        this.searchCommandHandler = searchCommandHandler;
        this.deletePageCommandHandler = deletePageCommandHandler;
        this.getContributionsCommandHandler = getContributionsCommandHandler;
        this.pageEditsCommandHandler = pageEditsCommandHandler;
        this.getEditCommandHandler = getEditCommandHandler;
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

    @GetMapping("-edits/{title}")
    public ResponseEntity<List<Edit>> edits(@PathVariable("title") String title,
                                            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                            @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        List<Edit> result = pageEditsCommandHandler.handler(SearchPaginationCommand.builder()
                .query(title)
                .page(page)
                .pageSize(pageSize)
                .build());
        return ResponseEntity.ok(result);
    }

    @GetMapping("-randomPage")
    public ResponseEntity<PageShort> random() {
        return getRandomPageCommandHandler.handler()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("-titleSearch/{query}")
    public ResponseEntity<List<PageShort>> titleSearch(@PathVariable("query") String query) {
        return ResponseEntity.ok(titleSearchCommandHandler.handler(query));
    }

    @GetMapping("-search/{query}")
    public ResponseEntity<List<PageShort>> search(@PathVariable("query") String query,
                                                  @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(searchCommandHandler.handler(SearchPaginationCommand.builder()
                .query(query)
                .page(page)
                .pageSize(pageSize)
                .build()));
    }

    @GetMapping("-contributions/{username}")
    public ResponseEntity<List<Edit>> contributions(@PathVariable("username") String username,
                                                    @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                    @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(getContributionsCommandHandler.handler(SearchPaginationCommand.builder()
                .query(username)
                .page(page)
                .pageSize(pageSize)
                .build()));
    }

    @DeleteMapping("{title}")
    public void delete(@PathVariable("title") String title, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {
        deletePageCommandHandler.handler(DeletePageCommand.builder()
                .title(title)
                .loginTokenId(toLoginToken(userToken))
                .build());
    }

    @GetMapping("{title}/block/{block}")
    public void block(@PathVariable("title") String title, @PathVariable("block") String block, @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {

    }

    @PutMapping("{title}")
    public void put(@PathVariable("title") String title, @RequestBody String body, @RequestHeader(value = AUTH_HEADER, required = false) String userToken, HttpServletRequest request) throws JsonProcessingException {
        PostPageModel postPageModel = objectMapper.readValue(body, PostPageModel.class);
        postPageCommandHandler.handler(PostPageCommand.builder()
                .title(title)
                .content(postPageModel.getContent())
                .categories(postPageModel.getCategories())
                .loginTokenId(toLoginToken(userToken))
                .ip(request.getRemoteAddr())
                .build());
    }

    @PostMapping("newPages")
    public ResponseEntity<List<PageShort>> newPages(@RequestBody String body) throws JsonProcessingException {
        PaginationModel paginationModel = objectMapper.readValue(body, PaginationModel.class);
        List<PageShort> pages = newPagesCommandHandler.handler(PaginationCommand.builder()
                .page(paginationModel.getPage())
                .pageSize(paginationModel.getPageSize())
                .build());
        return ResponseEntity.ok(pages);
    }

    @PostMapping("recentChanges")
    public ResponseEntity<List<PageShort>> recentChanges(@RequestBody String body) throws JsonProcessingException {
        PaginationModel paginationModel = objectMapper.readValue(body, PaginationModel.class);
        List<PageShort> pages = recentChangesCommandHandler.handler(PaginationCommand.builder()
                .page(paginationModel.getPage())
                .pageSize(paginationModel.getPageSize())
                .build());
        return ResponseEntity.ok(pages);
    }

    @GetMapping("-edit/{id}")
    public ResponseEntity<Edit> edit(@PathVariable("id") String id) {
        return getEditCommandHandler.handler(UUID.fromString(id))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
