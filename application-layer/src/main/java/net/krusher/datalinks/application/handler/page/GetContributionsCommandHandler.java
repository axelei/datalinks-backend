package net.krusher.datalinks.application.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.engineering.model.domain.user.UserService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.application.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.domain.model.page.Edit;
import net.krusher.datalinks.domain.model.user.User;

import java.util.List;

@ApplicationScoped
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class GetContributionsCommandHandler {

    private final PageService pageService;
    private final UserService userService;


    public List<Edit> handler(SearchPaginationCommand query) {
        query.validate();
        User user = userService.getByUsername(query.getQuery()).orElseThrow(() -> new EngineException(ErrorType.USER_NOT_FOUND, "User not found"));
        return pageService.findByUser(user, query.getPage(), query.getPageSize());
    }
}
