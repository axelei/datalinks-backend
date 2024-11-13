package net.krusher.datalinks.handler.page;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DeletePageCommand {
    private String title;
    private UUID loginTokenId;
}
