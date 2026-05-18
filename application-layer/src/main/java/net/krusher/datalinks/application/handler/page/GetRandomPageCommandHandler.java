package net.krusher.datalinks.application.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.domain.model.page.PageShort;

import java.util.Optional;

@ApplicationScoped
public class GetRandomPageCommandHandler {

    private final PageService pageService;

    @Inject
    public GetRandomPageCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public Optional<PageShort> handler() {
        int count = pageService.count();
        return pageService.allPages((int) (Math.random() * count), 1).stream().findFirst();
    }
}
