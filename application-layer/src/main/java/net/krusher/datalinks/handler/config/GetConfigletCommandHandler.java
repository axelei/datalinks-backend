package net.krusher.datalinks.handler.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.model.configlet.Configlet;
import net.krusher.datalinks.model.configlet.ConfigletKey;

import java.util.Optional;

@ApplicationScoped
public class GetConfigletCommandHandler {

    private final ConfigService configService;

    @Inject
    public GetConfigletCommandHandler(ConfigService configService) {
        this.configService = configService;
    }

    public Optional<Configlet> handler(GetConfigletCommand getConfigletCommand) {
        Configlet configlet = Configlet.builder().key(ConfigletKey.valueOf(getConfigletCommand.getKey())).value("value").build();
        return Optional.ofNullable(configlet);
    }
}
