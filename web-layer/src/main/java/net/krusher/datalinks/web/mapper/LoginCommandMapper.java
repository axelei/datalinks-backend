package net.krusher.datalinks.web.mapper;

import org.mapstruct.Mapper;
import net.krusher.datalinks.application.handler.user.LoginCommand;
import net.krusher.datalinks.web.model.LoginModel;

@Mapper(componentModel = "jsr330")
public interface LoginCommandMapper {
    LoginCommand toCommand(LoginModel model);
}
