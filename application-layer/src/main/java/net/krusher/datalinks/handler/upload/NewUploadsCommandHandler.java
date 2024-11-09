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

    private static final Set<Integer> PAGE_SIZES = Set.of(10, 20, 50, 100);

    private final UploadService uploadService;

    @Autowired
    public NewUploadsCommandHandler(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    public List<Upload> handler(PaginationCommand paginationCommand) {
        if (paginationCommand.getPage() < 0) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Page number must be positive");
        }
        if (!PAGE_SIZES.contains(paginationCommand.getPageSize())) {
            throw new EngineException(ErrorType.BAD_REQUEST, "Page size must be one of " + PAGE_SIZES);
        }
        return uploadService.newUploads(paginationCommand.getPage(), paginationCommand.getPageSize());
    }

}
