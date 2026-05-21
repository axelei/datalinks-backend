package net.krusher.datalinks.application.handler.upload;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.exception.ErrorType;
import net.krusher.datalinks.domain.model.page.PageShort;
import net.krusher.datalinks.domain.model.upload.Upload;

import java.util.List;
import java.util.Optional;

import static net.krusher.datalinks.application.handler.common.SlugifyProvider.SLUGIFY;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class FindUsagesCommandHandler {

    private final UploadService uploadService;


    public List<PageShort> handler(String filename) {
        Optional<Upload> upload = uploadService.findBySlug(SLUGIFY.slugify(filename));
        if (upload.isEmpty()) {
            throw new EngineException(ErrorType.FILE_NOT_FOUND, "File not found");
        }
        return uploadService.findUsages(upload.get().getId());
    }
}
