package net.krusher.datalinks.web.mapper;

import net.krusher.datalinks.application.handler.user.SignupCommand;
import net.krusher.datalinks.web.model.SignupModel;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface SignupCommandMapper {

    SignupCommand toCommand(SignupModel signupModel);
}
