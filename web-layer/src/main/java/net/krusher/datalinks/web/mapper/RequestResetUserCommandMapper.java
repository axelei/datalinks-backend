package net.krusher.datalinks.web.mapper;

import org.mapstruct.Mapper;
import net.krusher.datalinks.application.handler.user.RequestResetUserCommand;
import net.krusher.datalinks.web.model.PasswordResetRequestModel;

@Mapper(componentModel = "jakarta-cdi")
public interface RequestResetUserCommandMapper {
    RequestResetUserCommand toCommand(PasswordResetRequestModel model);
}
