package net.krusher.datalinks.mapper;

import net.krusher.datalinks.handler.user.SignupCommand;
import net.krusher.datalinks.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SignupMapper {

    User toModel(SignupCommand command);

}
