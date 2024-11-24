package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.page.PageShort;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PageMapper {

    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "categories", ignore = true)
    Page toModel(PageEntity entity);
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "categories", ignore = true)
    PageEntity toEntity(Page model);

    @Mapping(target = "creator", ignore = true)
    PageShort toModelShort(PageEntity entity);
    @Mapping(target = "creator", ignore = true)
    PageEntity toEntity(PageShort model);

}
