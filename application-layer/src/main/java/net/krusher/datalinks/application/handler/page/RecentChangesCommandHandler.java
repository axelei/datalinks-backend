package net.krusher.datalinks.application.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.application.handler.common.PaginationCommand;
import net.krusher.datalinks.domain.model.page.Edit;

import java.util.List;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class RecentChangesCommandHandler {

    private final PageService pageService;


    public List<Edit> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return pageService.editsSortBy("date", paginationCommand.getPage(), paginationCommand.getPageSize());
    }
}
