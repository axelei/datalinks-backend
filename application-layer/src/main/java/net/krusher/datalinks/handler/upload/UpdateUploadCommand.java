package net.krusher.datalinks.handler.upload;

import lombok.Builder;
import lombok.Data;

import java.io.InputStream;
import java.util.UUID;

@Data
@Builder
public class UpdateUploadCommand {
    private String filename;
    private String description;
    private UUID loginToken;
    private String ip;
}
