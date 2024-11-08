package net.krusher.datalinks.engineering.model.domain.upload;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UploadRepositoryBean extends JpaRepository<UploadEntity, UUID> {

}
