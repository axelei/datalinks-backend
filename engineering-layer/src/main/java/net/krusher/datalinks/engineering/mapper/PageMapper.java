package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.model.page.Page;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PageMapper {

    Page toModel(PageEntity entity);

    PageEntity toEntity(Page model);
}
