package net.krusher.datalinks.controller;

import net.krusher.datalinks.handler.category.CreateCategoryCommandHandler;
import net.krusher.datalinks.handler.category.DeleteCategoryCommandHandler;
import net.krusher.datalinks.handler.category.FindCategoriesCommandHandler;
import net.krusher.datalinks.handler.category.FindCategoryPagesCommandHandler;
import net.krusher.datalinks.handler.category.GetCategoriesCommandHandler;
import net.krusher.datalinks.handler.category.GetCategoryCommandHandler;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.page.PageShort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static net.krusher.datalinks.common.ControllerUtil.AUTH_HEADER;
import static net.krusher.datalinks.common.ControllerUtil.toLoginToken;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final GetCategoriesCommandHandler getCategoriesCommandHandler;
    private final DeleteCategoryCommandHandler deleteCategoryCommandHandler;
    private final CreateCategoryCommandHandler createCategoryCommandHandler;
    private final GetCategoryCommandHandler getCategoryCommandHandler;
    private final FindCategoriesCommandHandler findCategoriesCommandHandler;
    private final FindCategoryPagesCommandHandler findCategoryPagesCommandHandler;

    public CategoryController(GetCategoriesCommandHandler getCategoriesCommandHandler,
                              DeleteCategoryCommandHandler deleteCategoryCommandHandler,
                              CreateCategoryCommandHandler createCategoryCommandHandler,
                              GetCategoryCommandHandler getCategoryCommandHandler,
                              FindCategoriesCommandHandler findCategoriesCommandHandler,
                              FindCategoryPagesCommandHandler findCategoryPagesCommandHandler) {
        this.getCategoriesCommandHandler = getCategoriesCommandHandler;
        this.deleteCategoryCommandHandler = deleteCategoryCommandHandler;
        this.createCategoryCommandHandler = createCategoryCommandHandler;
        this.getCategoryCommandHandler = getCategoryCommandHandler;
        this.findCategoriesCommandHandler = findCategoriesCommandHandler;
        this.findCategoryPagesCommandHandler = findCategoryPagesCommandHandler;
    }

    @GetMapping("all")
    public ResponseEntity<List<Category>> getAll(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(getCategoriesCommandHandler.handler(PaginationCommand.builder()
                .page(page)
                .pageSize(pageSize)
                .build()));
    }

    @DeleteMapping("delete/{name}")
    public ResponseEntity<String> delete(@PathVariable("name") String name,
                                         @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {
        deleteCategoryCommandHandler.handler(name, toLoginToken(userToken));
        return ResponseEntity.ok("OK");
    }

    @PutMapping("add")
    public ResponseEntity<String> update(@RequestBody String name,
                                         @RequestHeader(value = AUTH_HEADER, required = false) String userToken) {
        createCategoryCommandHandler.handler(name, toLoginToken(userToken));
        return ResponseEntity.ok("OK");
    }

    @GetMapping("get/{name}")
    public ResponseEntity<Category> get(@PathVariable("name") String name) {
        return (getCategoryCommandHandler.handler(name).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @GetMapping("find/{query}")
    public ResponseEntity<List<Category>> find(@PathVariable("query") String query) {
        return ResponseEntity.ok(findCategoriesCommandHandler.handler(query));
    }

    @GetMapping("findPages/{query}")
    public ResponseEntity<List<PageShort>> findPages(@PathVariable("query") String query,
                                                     @RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                     @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(findCategoryPagesCommandHandler.handler(SearchPaginationCommand.builder()
                .query(query)
                .page(page)
                .pageSize(pageSize)
                .build()));
    }
}
