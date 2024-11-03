package net.krusher.datalinks.controller;

import io.vavr.control.Try;
import net.krusher.datalinks.engineering.model.domain.configlet.ConfigService;
import net.krusher.datalinks.handler.config.GetConfigletCommand;
import net.krusher.datalinks.handler.config.GetConfigletCommandHandler;
import net.krusher.datalinks.model.configlet.Configlet;
import net.krusher.datalinks.model.configlet.ConfigletKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final GetConfigletCommandHandler getConfigletCommandHandler;

    public ConfigController(GetConfigletCommandHandler getConfigletCommandHandler) {
        this.getConfigletCommandHandler = getConfigletCommandHandler;
    }

    @GetMapping("key/{key}")
    ResponseEntity<Configlet> get(@PathVariable("key") String key) {
        return getConfigletCommandHandler.handler(GetConfigletCommand.builder().key(key).build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("keys")
    Map<ConfigletKey, String> get() {
        return Arrays.stream(ConfigletKey.values()).collect(Collectors.toMap(name -> name, ConfigletKey::getDefaultValue));
    }

}
