package net.krusher.datalinks.engineering.model.domain.page;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class EditRepositoryBean implements PanacheRepositoryBase<EditEntity, UUID> {
}
