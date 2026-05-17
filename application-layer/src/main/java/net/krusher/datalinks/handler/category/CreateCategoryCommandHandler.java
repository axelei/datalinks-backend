package net.krusher.datalinks.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.handler.common.SlugifyProvider;
import net.krusher.datalinks.model.page.Category;

import java.util.UUID;

@ApplicationScoped
public class CreateCategoryCommandHandler {

    private final CategoryService categoryService;
    private final UserHelper userHelper;

    @Inject
    public CreateCategoryCommandHandler(CategoryService categoryService,
                                        UserHelper userHelper) {
        this.categoryService = categoryService;
        this.userHelper = userHelper;
    }

    @Transactional
    public void handler(String name, UUID loginToken) {
        if (!userHelper.isAdmin(loginToken)) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't create category");
        }
        categoryService.create(Category.builder()
                .name(name)
                .slug(SlugifyProvider.SLUGIFY.slugify(name))
                .build());
    }
}
