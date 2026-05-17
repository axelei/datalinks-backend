package net.krusher.datalinks.mapper;

import net.krusher.datalinks.handler.user.SignupCommand;
import net.krusher.datalinks.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface SignupMapper {

    User toModel(SignupCommand command);

}
