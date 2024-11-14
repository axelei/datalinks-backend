package net.krusher.datalinks.handler.page;

import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.model.page.Edit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class GetEditCommandHandler {

    private final PageService pageService;
    private final UserHelper userHelper;

    @Autowired
    public GetEditCommandHandler(PageService pageService, UserHelper userHelper) {
        this.pageService = pageService;
        this.userHelper = userHelper;
    }

    public Optional<Edit> handler(UUID editId) {
        return pageService.findEditById(editId);
    }
}
