package net.krusher.datalinks.controller;

import net.krusher.datalinks.handler.category.GetCategoriesCommandHandler;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.page.Category;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    private final GetCategoriesCommandHandler getCategoriesCommandHandler;

    public CategoryController(GetCategoriesCommandHandler getCategoriesCommandHandler) {
        this.getCategoriesCommandHandler = getCategoriesCommandHandler;
    }

    @GetMapping("all")
    public ResponseEntity<List<Category>> getAll(@RequestParam(name = "page", required = false, defaultValue = "0") int page,
                                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(getCategoriesCommandHandler.handler(PaginationCommand.builder()
                .page(page)
                .pageSize(pageSize)
                .build()));
    }
}
