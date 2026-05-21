package net.krusher.datalinks.application.handler.category;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.application.handler.common.SlugifyProvider;
import net.krusher.datalinks.domain.model.page.Category;

import java.util.UUID;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class CreateCategoryCommandHandler {

    private final CategoryService categoryService;
    private final UserHelper userHelper;


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
