package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.user.ResetTokenEntity;
import net.krusher.datalinks.model.user.ResetToken;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface ResetTokenMapper {
    ResetToken toModel(ResetTokenEntity entity);
    ResetTokenEntity toEntity(ResetToken model);
}
