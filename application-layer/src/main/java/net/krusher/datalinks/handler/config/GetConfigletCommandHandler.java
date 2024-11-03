package net.krusher.datalinks.handler.config;

import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.model.configlet.Configlet;
import net.krusher.datalinks.model.configlet.ConfigletKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetConfigletCommandHandler {

    private final ConfigService configService;

    @Autowired
    public GetConfigletCommandHandler(ConfigService configService) {
        this.configService = configService;
    }

    public Optional<Configlet> handler(GetConfigletCommand getConfigletCommand) {
        Configlet configlet = Configlet.builder().key(ConfigletKey.valueOf(getConfigletCommand.getKey())).value("value").build();
        return Optional.ofNullable(configlet);
    }
}
