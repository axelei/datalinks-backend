package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.configlet.ConfigletEntity;
import net.krusher.datalinks.model.configlet.Configlet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ConfigletMapper {

    Configlet toModel(ConfigletEntity entity);
    ConfigletEntity toEntity(Configlet model);
}
