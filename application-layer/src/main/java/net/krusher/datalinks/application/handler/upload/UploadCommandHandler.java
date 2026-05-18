package net.krusher.datalinks.application.handler.upload;

import io.vavr.control.Try;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import lombok.Setter;
import net.krusher.datalinks.application.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.upload.Upload;
import net.krusher.datalinks.domain.model.user.User;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.Optional;

import static net.krusher.datalinks.application.handler.common.SlugifyProvider.SLUGIFY;

@ApplicationScoped
public class UploadCommandHandler {

    private final UploadService uploadService;
    private final UserHelper userHelper;

    @Setter
    @ConfigProperty(name = "application.backend.url")
    String backendUrl;

    @Inject
    public UploadCommandHandler(UploadService uploadService, UserHelper userHelper) {
        this.uploadService = uploadService;
        this.userHelper = userHelper;
    }

    @Transactional
    public String handler(UploadCommand uploadCommand) {
        if (!userHelper.userCanUpload(uploadCommand.getLoginTokenId())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't upload");
        }
        Optional<User> user = userHelper.getUserFromLoginToken(uploadCommand.getLoginTokenId());
        Upload upload = Upload.builder()
                .filename(uploadCommand.getFilename())
                .inputStream(uploadCommand.getInputStream())
                .slug(SLUGIFY.slugify(uploadCommand.getFilename()))
                .description(uploadCommand.getDescription())
                .ipCreator(uploadCommand.getIp())
                .creatorId(user.map(User::getId).orElse(null))
                .build();
        Try.run(() -> uploadService.save(upload))
                .onFailure(throwable -> {
                    throw new EngineException(ErrorType.UPLOAD_ERROR, "Upload error", throwable);
                });
        return backendUrl + "/file/get/" + upload.getSlug();
    }
}
