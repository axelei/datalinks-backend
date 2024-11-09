package net.krusher.datalinks.handler.page;

import com.github.slugify.Slugify;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.model.page.PageShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetRandomPageCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    private final Slugify slugify = Slugify.builder().build();

    @Autowired
    public GetRandomPageCommandHandler(PageService pageService, UserHelper userHelper) {
        this.pageService = pageService;
        this.userHelper = userHelper;
    }

    public Optional<PageShort> handler() {
        int count = pageService.count();
        return pageService.allPages((int) (Math.random() * count), 1).stream().findFirst();
    }
}
