package net.krusher.datalinks.model.configlet;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Configlet {
    private String key;
    private String value;
}
