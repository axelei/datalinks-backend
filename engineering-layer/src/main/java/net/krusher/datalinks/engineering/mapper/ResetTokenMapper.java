package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.user.LoginTokenEntity;
import net.krusher.datalinks.engineering.model.domain.user.ResetTokenEntity;
import net.krusher.datalinks.model.user.LoginToken;
import net.krusher.datalinks.model.user.ResetToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ResetTokenMapper {
    ResetToken toModel(ResetTokenEntity entity);
    ResetTokenEntity toEntity(ResetToken model);
}
