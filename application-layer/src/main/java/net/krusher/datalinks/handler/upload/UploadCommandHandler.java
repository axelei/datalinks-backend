package net.krusher.datalinks.handler.upload;

import io.vavr.control.Try;
import lombok.Setter;
import net.krusher.datalinks.common.UserHelper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.upload.Upload;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static net.krusher.datalinks.handler.common.SlugifyProvider.SLUGIFY;

@Service
public class UploadCommandHandler {

    private final UploadService uploadService;
    private final UserHelper userHelper;

    @Setter
    @Value("${application.backend.url}")
    private String backendUrl;

    @Autowired
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
