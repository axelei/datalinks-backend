package net.krusher.datalinks.handler.category;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.CategoryService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CreateCategoryCommandHandler {

    private final CategoryService categoryService;
    private final UserHelper userHelper;

    @Autowired
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
        categoryService.create(name);
    }
}