package net.krusher.datalinks.handler.config;

import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.model.configlet.Configlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class GetConfigCommandHandler {

    private final ConfigService configService;

    @Autowired
    public GetConfigCommandHandler(ConfigService configService) {
        this.configService = configService;
    }

    @Transactional
    public Set<Configlet> handler() {
        return configService.getConfig();
    }
}
