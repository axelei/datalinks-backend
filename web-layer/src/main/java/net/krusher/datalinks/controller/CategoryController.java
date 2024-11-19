package net.krusher.datalinks.controller;

import net.krusher.datalinks.handler.category.CreateCategoryCommandHandler;
import net.krusher.datalinks.handler.category.DeleteCategoryCommandHandler;
import net.krusher.datalinks.handler.category.GetCategoriesCommandHandler;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.page.Category;
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

    public CategoryController(GetCategoriesCommandHandler getCategoriesCommandHandler,
                              DeleteCategoryCommandHandler deleteCategoryCommandHandler,
                              CreateCategoryCommandHandler createCategoryCommandHandler) {
        this.getCategoriesCommandHandler = getCategoriesCommandHandler;
        this.deleteCategoryCommandHandler = deleteCategoryCommandHandler;
        this.createCategoryCommandHandler = createCategoryCommandHandler;
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
}
