package net.krusher.datalinks.handler.upload;

import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.handler.common.PaginationCommand;
import net.krusher.datalinks.model.upload.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class NewUploadsCommandHandler {

    private final UploadService uploadService;

    @Autowired
    public NewUploadsCommandHandler(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    public List<Upload> handler(PaginationCommand paginationCommand) {
        paginationCommand.validate();
        return uploadService.newUploads(paginationCommand.getPage(), paginationCommand.getPageSize());
    }

}
