package net.krusher.datalinks.application.handler.upload;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.upload.Upload;

import java.util.Optional;

import static net.krusher.datalinks.application.handler.common.SlugifyProvider.SLUGIFY;

@ApplicationScoped
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class GetFileCommandHandler {

    private final UploadService uploadService;
    private final UserHelper userHelper;


    public Upload handler(GetFileCommand getFileCommand) {
        Optional<Upload> upload = uploadService.findBySlug(SLUGIFY.slugify(getFileCommand.getFilename()));
        if (upload.isEmpty()) {
            throw new EngineException(ErrorType.FILE_NOT_FOUND, "File not found");
        }
        if (!userHelper.userCanSeeFile(upload.get(), getFileCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't see file");
        }
        return upload.get();
    }
}
