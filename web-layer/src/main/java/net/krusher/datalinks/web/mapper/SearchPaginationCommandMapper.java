package net.krusher.datalinks.web.mapper;

import org.mapstruct.Mapper;
import net.krusher.datalinks.application.handler.common.SearchPaginationCommand;
import net.krusher.datalinks.web.model.SearchPaginationModel;

@Mapper(componentModel = "jsr330")
public interface SearchPaginationCommandMapper {
    SearchPaginationCommand toCommand(SearchPaginationModel model);
}
