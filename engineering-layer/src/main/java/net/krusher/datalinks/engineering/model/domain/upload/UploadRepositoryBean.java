package net.krusher.datalinks.engineering.model.domain.upload;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UploadRepositoryBean implements PanacheRepositoryBase<UploadEntity, UUID> {
}
