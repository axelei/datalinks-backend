package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.page.EditEntity;
import net.krusher.datalinks.model.page.Edit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EditMapper {

    @Mapping(target = "page", ignore = true)
    @Mapping(target = "user", ignore = true)
    Edit toModel(EditEntity entity);
    @Mapping(target = "page", ignore = true)
    @Mapping(target = "user", ignore = true)
    EditEntity toEntity(Edit model);


}
