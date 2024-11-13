package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.page.EditEntity;
import net.krusher.datalinks.model.page.Edit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EditMapper {

    Edit toModel(EditEntity entity);
    EditEntity toEntity(Edit model);


}
