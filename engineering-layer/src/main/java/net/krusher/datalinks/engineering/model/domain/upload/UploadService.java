package net.krusher.datalinks.engineering.model.domain.upload;

import io.vavr.control.Try;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.Setter;
import net.krusher.datalinks.engineering.mapper.UploadMapper;
import net.krusher.datalinks.exception.EngineException;
import net.krusher.datalinks.exception.ErrorType;
import net.krusher.datalinks.model.upload.Upload;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.List;
import java.util.Optional;

@Service
public class UploadService {

    @Setter
    @Value("${application.upload.dir}")
    private String uploadDir;

    private final UploadMapper uploadMapper;
    private final UploadRepositoryBean uploadRepositoryBean;
    private final EntityManager entityManager;

    public UploadService(UploadMapper uploadMapper, UploadRepositoryBean uploadRepositoryBean, EntityManager entityManager) {
        this.uploadMapper = uploadMapper;
        this.uploadRepositoryBean = uploadRepositoryBean;
        this.entityManager = entityManager;
    }

    public void save(Upload upload) throws IOException {
        byte[] bytes = upload.getInputStream().readAllBytes();
        String md5 = Try.of(() -> MessageDigest.getInstance("MD5"))
                .map(digest -> Hex.encodeHexString(digest.digest(bytes)))
                .getOrElseThrow(e -> new EngineException(ErrorType.UPLOAD_ERROR, "MD5 algorithm not found", e));
        String uploadPath = getUploadPath(md5, upload.getSlug());
        if (findBySlug(upload.getSlug()).isPresent()) {
            throw new EngineException(ErrorType.UPLOAD_ERROR, "File already exists");
        }
        upload.setMd5(md5);
        Path path = Path.of(uploadDir + uploadPath);
        if (!path.getParent().toFile().exists() && !path.getParent().toFile().mkdirs()) {
            throw new EngineException(ErrorType.UPLOAD_ERROR, "Can't create directory");
        }
        Files.write(path, bytes);
        uploadRepositoryBean.save(uploadMapper.toEntity(upload));
    }

    public Optional<Upload> findBySlug(String slug) {
        Example<UploadEntity> example = Example.of(UploadEntity.builder().slug(slug).build());
        List<UploadEntity> result = uploadRepositoryBean.findAll(example);
        Optional<Upload> upload = result.stream().findFirst().map(uploadMapper::toModel);
        if (upload.isPresent()) {
            String uploadPath = getUploadPath(upload.get().getMd5(), upload.get().getSlug());
            upload.get().setInputStream(Try.of(() -> Files.newInputStream(Path.of(uploadDir + uploadPath)))
                    .getOrElseThrow(e -> new EngineException(ErrorType.UPLOAD_ERROR, "File not found", e)));
        }
        return upload;
    }

    private String getUploadPath(String md5, String filename) {
        return md5.charAt(0) + "/" + md5.charAt(0) + md5.charAt(1) + "/" + filename;
    }

    public List<Upload> newUploads(int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UploadEntity> cq = cb.createQuery(UploadEntity.class);
        cq.orderBy(cb.desc(cq.from(UploadEntity.class).get("creationDate")));
        TypedQuery<UploadEntity> query = entityManager.createQuery(cq);
        return query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList().stream().map(uploadMapper::toModel).toList();
    }
}
