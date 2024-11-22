package net.krusher.datalinks.handler.tasks;

import lombok.extern.log4j.Log4j2;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.handler.common.SlugifyProvider;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UnlinkerCommandHandler {

    private final PageService pageService;
    private final LinkProcessorHelper linkProcessorHelper;

    @Autowired
    public UnlinkerCommandHandler(PageService pageService, LinkProcessorHelper linkProcessorHelper) {
        this.pageService = pageService;
        this.linkProcessorHelper = linkProcessorHelper;
    }

    public void handler() {
        final List<String> titles = pageService.findAllTitles().stream().sorted(Comparator.comparingInt(String::length).reversed()).toList();
        titles.parallelStream().forEach(title -> {
            log.info("Processing title: {}", title);
            String slug = SlugifyProvider.SLUGIFY.slugify(title);
            Optional<Page> page = pageService.findBySlug(slug);
            if (page.isEmpty()) {
                return;
            }
            linkProcessorHelper.processUnlinkersPage(page.get(), titles);
        });
    }



}
