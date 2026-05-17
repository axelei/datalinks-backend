package net.krusher.datalinks.handler.page;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.model.page.Edit;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class GetEditCommandHandler {

    private final PageService pageService;

    @Inject
    public GetEditCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    public Optional<Edit> handler(UUID editId) {
        return pageService.findEditById(editId);
    }
}
