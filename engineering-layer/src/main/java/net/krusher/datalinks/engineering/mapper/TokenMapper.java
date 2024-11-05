package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.user.LoginTokenEntity;
import net.krusher.datalinks.model.user.LoginToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TokenMapper {
    LoginToken toModel(LoginTokenEntity entity);
    LoginTokenEntity toEntity(LoginToken model);
}
