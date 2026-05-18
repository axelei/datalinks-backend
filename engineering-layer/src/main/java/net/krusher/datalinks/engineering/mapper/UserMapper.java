package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.domain.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface UserMapper {
    User toModel(UserEntity entity);
    UserEntity toEntity(User model);
}
