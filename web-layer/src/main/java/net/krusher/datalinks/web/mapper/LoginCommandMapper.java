package net.krusher.datalinks.web.mapper;

import org.mapstruct.Mapper;
import net.krusher.datalinks.application.handler.user.LoginCommand;
import net.krusher.datalinks.web.model.LoginModel;

@Mapper(componentModel = "jakarta-cdi")
public interface LoginCommandMapper {
    LoginCommand toCommand(LoginModel model);
}
