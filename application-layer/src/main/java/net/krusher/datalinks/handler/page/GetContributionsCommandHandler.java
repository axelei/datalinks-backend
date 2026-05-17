package net.krusher.datalinks.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.model.page.Edit;
import net.krusher.datalinks.model.user.User;

import java.util.List;

@ApplicationScoped
public class GetContributionsCommandHandler {

    private final PageService pageService;
    private final UserService userService;

    @Inject
    public GetContributionsCommandHandler(PageService pageService, UserService userService) {
        this.pageService = pageService;
        this.userService = userService;
    }

    public List<Edit> handler(SearchPaginationCommand query) {
        query.validate();
        User user = userService.getByUsername(query.getQuery()).orElseThrow(() -> new EngineException(ErrorType.USER_NOT_FOUND, "User not found"));
        return pageService.findByUser(user, query.getPage(), query.getPageSize());
    }
}
