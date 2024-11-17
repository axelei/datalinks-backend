package net.krusher.datalinks.controller;

import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.handler.search.SearchCommandHandler;
import net.krusher.datalinks.handler.search.TitleSearchCommandHandler;
import net.krusher.datalinks.model.search.Foundling;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    private final TitleSearchCommandHandler titleSearchCommandHandler;
    private final SearchCommandHandler searchCommandHandler;

    @Autowired
    public SearchController(final TitleSearchCommandHandler titleSearchCommandHandler,
                            final SearchCommandHandler searchCommandHandler) {
        this.titleSearchCommandHandler = titleSearchCommandHandler;
        this.searchCommandHandler = searchCommandHandler;
    }

    @GetMapping("titleSearch/{query}")
    public ResponseEntity<List<Foundling>> titleSearch(@PathVariable("query") String query) {
        return ResponseEntity.ok(titleSearchCommandHandler.handler(query));
    }

    @GetMapping("full/{query}")
    public ResponseEntity<List<Foundling>> search(@PathVariable("query") String query,
                                                  @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                  @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(searchCommandHandler.handler(SearchPaginationCommand.builder()
                .query(query)
                .page(page)
                .pageSize(pageSize)
                .build()));
    }
}
