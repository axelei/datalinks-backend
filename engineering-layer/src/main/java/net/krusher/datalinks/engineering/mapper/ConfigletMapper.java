package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.configlet.ConfigletEntity;
import net.krusher.datalinks.domain.model.configlet.Configlet;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface ConfigletMapper {

    Configlet toModel(ConfigletEntity entity);
    ConfigletEntity toEntity(Configlet model);
}
