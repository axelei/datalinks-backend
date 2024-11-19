package net.krusher.datalinks.handler.category;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeleteCategoryCommandHandler {

    private final CategoryService categoryService;
    private final UserHelper userHelper;

    @Autowired
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
        categoryService.delete(name);
    }
}