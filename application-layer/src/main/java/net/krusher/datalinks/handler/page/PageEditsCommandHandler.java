package net.krusher.datalinks.handler.page;

import com.github.slugify.Slugify;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.model.page.Edit;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static net.krusher.datalinks.handler.common.SlugifyProvider.SLUGIFY;

@Service
public class PageEditsCommandHandler {

    private final PageService pageService;

    @Autowired
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