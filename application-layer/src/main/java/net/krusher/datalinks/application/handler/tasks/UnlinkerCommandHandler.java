package net.krusher.datalinks.application.handler.tasks;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import net.krusher.datalinks.engineering.model.domain.page.PageService;
import net.krusher.datalinks.application.handler.common.SlugifyProvider;
import net.krusher.datalinks.domain.model.page.Page;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@JBossLog
@ApplicationScoped
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class UnlinkerCommandHandler {

    private final PageService pageService;
    private final LinkProcessorHelper linkProcessorHelper;


    public void handler() {
        final List<String> titles = pageService.findAllTitles().stream().sorted(Comparator.comparingInt(String::length).reversed()).toList();
        titles.parallelStream().forEach(title -> {
            log.infof("Processing title: %s", title);
            String slug = SlugifyProvider.SLUGIFY.slugify(title);
            Optional<Page> page = pageService.findBySlug(slug);
            if (page.isEmpty()) {
                return;
            }
            linkProcessorHelper.processUnlinkersPage(page.get(), titles);
        });
    }



}
