package net.krusher.datalinks.application.handler.tasks;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.extern.jbosslog.JBossLog;
import net.krusher.datalinks.engineering.model.domain.page.PageService;

import java.util.Comparator;
import java.util.List;

import lombok.AllArgsConstructor;
@JBossLog
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class UnlinkerCommandHandler {

    private final PageService pageService;
    private final LinkProcessorHelper linkProcessorHelper;


    public void handler() {
        final List<String> titles = pageService.findAllTitles().stream().sorted(Comparator.comparingInt(String::length).reversed()).toList();
        titles.parallelStream().forEach(title -> {
            log.infof("Processing title: %s", title);
            linkProcessorHelper.processUnlinkerForTitle(title, titles);
        });
    }



}
