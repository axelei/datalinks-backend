package net.krusher.datalinks.model.configlet;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Configlet {
    private ConfigletKey key;
    private String value;

    public static Configlet of(ConfigletKey key, String value) {
        return Configlet.builder().key(key).value(value).build();
    }
}
