package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.page.CategoryEntity;
import net.krusher.datalinks.model.page.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toModel(CategoryEntity entity);
    CategoryEntity toEntity(Category model);

}
