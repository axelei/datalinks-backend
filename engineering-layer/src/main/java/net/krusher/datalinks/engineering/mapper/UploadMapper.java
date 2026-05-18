package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.upload.UploadEntity;
import net.krusher.datalinks.domain.model.upload.Upload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "jakarta-cdi")
public interface UploadMapper {
    Upload toModel(UploadEntity entity);
    UploadEntity toEntity(Upload model);
}
