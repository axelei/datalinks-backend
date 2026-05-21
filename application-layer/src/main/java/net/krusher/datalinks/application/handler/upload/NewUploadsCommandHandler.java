package net.krusher.datalinks.application.handler.upload;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.application.handler.common.PaginationCommand;
import net.krusher.datalinks.domain.model.upload.Upload;

import java.util.List;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class NewUploadsCommandHandler {

    private final UploadService uploadService;


    public List<Upload> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return uploadService.newUploads(paginationCommand.getPage(), paginationCommand.getPageSize());
    }

}
