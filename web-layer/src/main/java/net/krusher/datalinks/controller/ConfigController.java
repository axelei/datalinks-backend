package net.krusher.datalinks.controller;

import io.vavr.control.Try;
import net.krusher.datalinks.model.configlet.Configlet;
import net.krusher.datalinks.model.configlet.ConfigletKey;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @GetMapping("key/{key}")
    ResponseEntity<Configlet> get(@PathVariable("key") String key) {
        return Try.of(() -> Configlet.builder().key(ConfigletKey.valueOf(key)).value("value").build())
                .map(ResponseEntity::ok)
                .getOrElse(ResponseEntity.notFound().build());
    }

    @GetMapping("keys")
    Map<ConfigletKey, String> get() {
        return Map.of(
                ConfigletKey.SITE_TITLE, ConfigletKey.SITE_TITLE.getDefaultValue(),
                ConfigletKey.SITE_DESCRIPTION, ConfigletKey.SITE_DESCRIPTION.getDefaultValue(),
                ConfigletKey.SITE_IMAGE, ConfigletKey.SITE_IMAGE.getDefaultValue()
        );
    }

}
