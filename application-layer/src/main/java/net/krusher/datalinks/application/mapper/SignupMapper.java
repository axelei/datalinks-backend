package net.krusher.datalinks.application.mapper;

import net.krusher.datalinks.application.handler.user.SignupCommand;
import net.krusher.datalinks.domain.model.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface SignupMapper {

    User toModel(SignupCommand command);

}
