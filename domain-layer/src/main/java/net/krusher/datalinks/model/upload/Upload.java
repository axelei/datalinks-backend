package net.krusher.datalinks.model.upload;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Builder
@Data
public class Upload {
    private UUID id;
}
