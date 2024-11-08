package net.krusher.datalinks.engineering.mapper;

import net.krusher.datalinks.engineering.model.domain.upload.UploadEntity;
import net.krusher.datalinks.model.upload.Upload;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UploadMapper {
    Upload toModel(UploadEntity entity);
    UploadEntity toEntity(Upload model);
}
