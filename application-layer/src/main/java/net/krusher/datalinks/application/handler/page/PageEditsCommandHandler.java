package net.krusher.datalinks.application.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.application.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.domain.model.page.Edit;
import net.krusher.datalinks.domain.model.page.Page;

import java.util.List;
import java.util.Optional;

import static net.krusher.datalinks.application.handler.common.SlugifyProvider.SLUGIFY;

@ApplicationScoped
public class PageEditsCommandHandler {

    private final PageService pageService;

    @Inject
    public PageEditsCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public List<Edit> handler(SearchPaginationCommand query) {
        query.validate();
        Optional<Page> page = pageService.findBySlug(SLUGIFY.slugify(query.getQuery()));
        if (page.isEmpty()) {
            return List.of();
        }
        return pageService.findByPage(page.get(), query.getPage(), query.getPageSize());
    }
}
