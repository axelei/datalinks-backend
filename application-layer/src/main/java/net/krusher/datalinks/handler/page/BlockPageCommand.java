package net.krusher.datalinks.handler.page;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class BlockPageCommand {
    private String title;
    private int readBlock;
    private int writeBlock;
    private UUID loginToken;
}
