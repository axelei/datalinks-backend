package net.krusher.datalinks.handler.page;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetPageCommand {
    private String title;
    private UUID loginTokenId;
}
