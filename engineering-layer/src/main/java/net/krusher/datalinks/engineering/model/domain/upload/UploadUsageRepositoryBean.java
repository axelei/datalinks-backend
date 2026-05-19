package net.krusher.datalinks.engineering.model.domain.upload;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UploadUsageRepositoryBean implements PanacheRepositoryBase<UploadUsageEntity, UploadUsageId> {
}
