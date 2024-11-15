package net.krusher.datalinks.handler.upload;

import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.page.PageShort;
import net.krusher.datalinks.model.upload.Upload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static net.krusher.datalinks.handler.common.SlugifyProvider.SLUGIFY;

@Service
public class FindUsagesCommandHandler {

    private final UploadService uploadService;

    @Autowired
    public FindUsagesCommandHandler(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    public List<PageShort> handler(String filename) {
        Optional<Upload> upload = uploadService.findBySlug(SLUGIFY.slugify(filename));
        if (upload.isEmpty()) {
            throw new EngineException(ErrorType.FILE_NOT_FOUND, "File not found");
        }
        return uploadService.findUsages(upload.get().getId());
    }
}