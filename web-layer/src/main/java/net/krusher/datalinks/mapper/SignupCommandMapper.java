package net.krusher.datalinks.mapper;

import net.krusher.datalinks.handler.user.SignupCommand;
import net.krusher.datalinks.model.SignupModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SignupCommandMapper {

    SignupCommand toCommand(SignupModel signupModel);
}
