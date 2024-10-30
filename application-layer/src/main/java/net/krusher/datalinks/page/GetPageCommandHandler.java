package net.krusher.datalinks.page;

import net.krusher.datalinks.engineering.page.PageRepository;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetPageCommandHandler {

    private final PageRepository pageRepository;

    @Autowired
    public GetPageCommandHandler(PageRepository pageRepository) {
        this.pageRepository = pageRepository;
    }

    public Optional<Page> handler(GetPageCommand getPageCommand) {
        return pageRepository.get(getPageCommand.getTitle());
    }
}
