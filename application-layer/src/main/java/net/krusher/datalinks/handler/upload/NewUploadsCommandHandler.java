package net.krusher.datalinks.handler.upload;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.upload.Upload;

import java.util.List;

@ApplicationScoped
public class NewUploadsCommandHandler {

    private final UploadService uploadService;

    @Inject
    public NewUploadsCommandHandler(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    public List<Upload> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return uploadService.newUploads(paginationCommand.getPage(), paginationCommand.getPageSize());
    }

}
