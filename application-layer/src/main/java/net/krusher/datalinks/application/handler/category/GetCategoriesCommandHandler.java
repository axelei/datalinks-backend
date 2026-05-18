package net.krusher.datalinks.application.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.application.handler.common.PaginationCommand;
import net.krusher.datalinks.domain.model.page.Category;

import java.util.List;

@ApplicationScoped
public class GetCategoriesCommandHandler {

    private final CategoryService categoryService;

    @Inject
    public GetCategoriesCommandHandler(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    public List<Category> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return categoryService.allCategories(paginationCommand.getPage(), paginationCommand.getPageSize());
    }
}
