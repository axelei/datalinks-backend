package net.krusher.datalinks.handler.common;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class SearchPaginationCommand extends PaginationCommand{
    private String query;
}
