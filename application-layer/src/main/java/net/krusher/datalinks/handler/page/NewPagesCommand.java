package net.krusher.datalinks.handler.page;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class NewPagesCommand {
    private int page;
    private int pageSize;
}
