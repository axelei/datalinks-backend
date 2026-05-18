package net.krusher.datalinks.application.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.application.handler.common.SlugifyProvider;

import java.util.UUID;

@ApplicationScoped
public class DeleteCategoryCommandHandler {

    private final CategoryService categoryService;
    private final UserHelper userHelper;

    @Inject
    public DeleteCategoryCommandHandler(CategoryService categoryService,
                                        UserHelper userHelper) {
        this.categoryService = categoryService;
        this.userHelper = userHelper;
    }

    @Transactional
    public void handler(String name, UUID loginToken) {
        if (!userHelper.isAdmin(loginToken)) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't delete category");
        }
        categoryService.deleteBySlug(SlugifyProvider.SLUGIFY.slugify(name));
    }
}
