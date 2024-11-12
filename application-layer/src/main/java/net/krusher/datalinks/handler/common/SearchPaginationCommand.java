package net.krusher.datalinks.handler.common;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchPaginationCommand {
    private String query;
    private int page;
    private int pageSize;
}
