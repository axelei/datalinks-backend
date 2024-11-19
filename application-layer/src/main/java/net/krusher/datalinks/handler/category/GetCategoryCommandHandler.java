package net.krusher.datalinks.handler.category;

import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.page.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GetCategoryCommandHandler {

    private final CategoryService categoryService;

    @Autowired
    public GetCategoryCommandHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public Optional<Category> handler(String name) {
        return categoryService.getCategory(name);
    }
}