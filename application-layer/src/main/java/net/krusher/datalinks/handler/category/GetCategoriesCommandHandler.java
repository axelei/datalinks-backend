package net.krusher.datalinks.handler.category;

import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.page.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetCategoriesCommandHandler {

    private final CategoryService categoryService;

    @Autowired
    public GetCategoriesCommandHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<Category> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return categoryService.allCategories(paginationCommand.getPage(), paginationCommand.getPageSize());
    }
}