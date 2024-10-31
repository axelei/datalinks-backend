package net.krusher.datalinks.page;

import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostPageCommandHandler {

    private final PageService pageService;

    @Autowired
    public PostPageCommandHandler(PageService pageService) {
        this.pageService = pageService;
    }

    @Transactional
    public void handler(PostPageCommand postPageCommand) {
        pageService.findByTitle(postPageCommand.getTitle())
                .ifPresentOrElse(page -> updatePage(page, postPageCommand), () -> createPage(postPageCommand));
    }

    private void createPage(PostPageCommand postPageCommand) {
        Page page = Page.builder()
                .title(postPageCommand.getTitle())
                .content(postPageCommand.getContent())
                .build();
        pageService.save(page);
    }

    private void updatePage(Page page, PostPageCommand postPageCommand) {
        page.setContent(postPageCommand.getContent());
        pageService.save(page);
    }
}
