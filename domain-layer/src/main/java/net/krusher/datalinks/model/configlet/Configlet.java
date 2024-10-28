package net.krusher.datalinks.model.configlet;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Configlet {
    private ConfigletKey key;
    private String value;
}
