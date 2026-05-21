package net.krusher.datalinks.web.mapper;

import org.mapstruct.Mapper;
import net.krusher.datalinks.application.handler.common.PaginationCommand;
import net.krusher.datalinks.web.model.PaginationModel;

@Mapper(componentModel = "jsr330")
public interface PaginationCommandMapper {
    PaginationCommand toCommand(PaginationModel model);
}
