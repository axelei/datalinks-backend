package net.krusher.datalinks.application.handler.upload;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.upload.Upload;

import static net.krusher.datalinks.application.handler.common.SlugifyProvider.SLUGIFY;

@ApplicationScoped
public class DeleteUploadCommandHandler {

    private final UploadService uploadService;
    private final UserHelper userHelper;

    @Inject
    public DeleteUploadCommandHandler(UploadService uploadService, UserHelper userHelper) {
        this.uploadService = uploadService;
        this.userHelper = userHelper;
    }

    @Transactional
    public void handler(DeleteUploadCommand uploadCommand) {
        Upload upload = uploadService.findBySlug(SLUGIFY.slugify(uploadCommand.getFilename()))
                .orElseThrow(() -> new EngineException(ErrorType.UPLOAD_ERROR, "Upload not found"));
        if (!userHelper.userCanDeleteUpload(uploadCommand.getLoginToken())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't delete upload");
        }
        uploadService.delete(upload);
    }
}
