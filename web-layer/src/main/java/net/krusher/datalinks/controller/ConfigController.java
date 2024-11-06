package net.krusher.datalinks.controller;

import net.krusher.datalinks.handler.config.GetConfigCommandHandler;
import net.krusher.datalinks.handler.config.GetConfigletCommand;
import net.krusher.datalinks.handler.config.GetConfigletCommandHandler;
import net.krusher.datalinks.model.configlet.Configlet;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/config")
public class ConfigController {

    private final GetConfigletCommandHandler getConfigletCommandHandler;
    private final GetConfigCommandHandler getConfigCommandHandler;

    public ConfigController(GetConfigletCommandHandler getConfigletCommandHandler, GetConfigCommandHandler getConfigCommandHandler) {
        this.getConfigletCommandHandler = getConfigletCommandHandler;
        this.getConfigCommandHandler = getConfigCommandHandler;
    }

    @GetMapping("key/{key}")
    ResponseEntity<Configlet> get(@PathVariable("key") String key) {
        return getConfigletCommandHandler.handler(GetConfigletCommand.builder().key(key).build())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("all")
    Set<Configlet> get() {
        return getConfigCommandHandler.handler();
    }

}
