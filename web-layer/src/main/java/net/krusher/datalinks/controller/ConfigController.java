package net.krusher.datalinks.controller;

import net.krusher.datalinks.model.configlet.Configlet;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigController {

    @GetMapping("/config/{key}")
    Configlet get(@PathVariable("key") String key) {
        return Configlet.builder().key(key).value("value").build();
    }
}
