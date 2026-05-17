package net.krusher.datalinks.engineering.model.domain.configlet;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfigletRepositoryBean implements PanacheRepositoryBase<ConfigletEntity, String> {
}
