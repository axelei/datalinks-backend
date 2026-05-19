package net.krusher.datalinks.application.handler.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetConfigletCommand {
    private String key;
}
