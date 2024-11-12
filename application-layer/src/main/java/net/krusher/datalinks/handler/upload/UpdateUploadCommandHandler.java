package net.krusher.datalinks.handler.upload;

import com.github.slugify.Slugify;
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

@Service
public class UpdateUploadCommandHandler {

    private final UploadService uploadService;
    private final UserHelper userHelper;

    @Setter
    @Value("${application.backend.url}")
    private String backendUrl;

    private final Slugify slugify = Slugify.builder().build();

    @Autowired
    public UpdateUploadCommandHandler(UploadService uploadService, UserHelper userHelper) {
        this.uploadService = uploadService;
        this.userHelper = userHelper;
    }

    @Transactional
    public void handler(UpdateUploadCommand uploadCommand) {
        Upload upload = uploadService.findBySlug(slugify.slugify(uploadCommand.getFilename()))
                .orElseThrow(() -> new EngineException(ErrorType.UPLOAD_ERROR, "Upload not found"));
        if (!userHelper.userCanUpdateUpload(upload, uploadCommand.getLoginToken())) {
            throw new EngineException(ErrorType.PERMISSIONS_ERROR, "User can't update upload");
        }
        upload.setDescription(uploadCommand.getDescription());
        upload.setIpModifier(uploadCommand.getIp());
        uploadService.update(upload);
    }
}
