package net.krusher.datalinks.controller;

import net.krusher.datalinks.model.configlet.Configlet;
import net.krusher.datalinks.model.configlet.ConfigletKey;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @GetMapping("{key}")
    Configlet get(@PathVariable("key") String key) {
        return Configlet.builder().key(ConfigletKey.valueOf(key)).value("value").build();
    }
}
