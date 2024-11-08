package net.krusher.datalinks.handler.upload;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetFileCommand {
    private String filename;
    private UUID loginTokenId;
}
