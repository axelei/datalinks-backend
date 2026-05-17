package net.krusher.datalinks.handler.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.model.configlet.Configlet;

import java.util.Set;

@ApplicationScoped
public class GetConfigCommandHandler {

    private final ConfigService configService;

    @Inject
    public GetConfigCommandHandler(ConfigService configService) {
        this.configService = configService;
    }

    @Transactional
    public Set<Configlet> handler() {
        return configService.getConfig();
    }
}
