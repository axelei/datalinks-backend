package net.krusher.datalinks.handler.upload;

import com.github.slugify.Slugify;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.upload.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetFileCommandHandler {

    private final UploadService uploadService;
    private final UserHelper userHelper;

    private final Slugify slugify = Slugify.builder().build();

    @Autowired
    public GetFileCommandHandler(UploadService uploadService, UserHelper userHelper) {
        this.uploadService = uploadService;
        this.userHelper = userHelper;
    }

    public Upload handler(GetFileCommand getFileCommand) {
        Optional<Upload> upload = uploadService.findBySlug(slugify.slugify(getFileCommand.getFilename()));
        if (upload.isEmpty()) {
            throw new EngineException(ErrorType.UPLOAD_ERROR, "File not found");
        }
        if (!userHelper.userCanSeeFile(upload.get(), getFileCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't see file");
        }
        return upload.get();
    }
}
