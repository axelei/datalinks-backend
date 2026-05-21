package net.krusher.datalinks.web.mapper;

import org.mapstruct.Mapper;
import net.krusher.datalinks.application.handler.upload.UpdateUploadCommand;
import net.krusher.datalinks.web.model.UpdateUploadRequestModel;

@Mapper(componentModel = "jakarta-cdi")
public interface UpdateUploadCommandMapper {
    UpdateUploadCommand toCommand(UpdateUploadRequestModel model);
}
