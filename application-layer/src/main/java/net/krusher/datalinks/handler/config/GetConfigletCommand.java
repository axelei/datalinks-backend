package net.krusher.datalinks.handler.config;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetConfigletCommand {
    private String key;
}
