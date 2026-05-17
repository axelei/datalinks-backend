package net.krusher.datalinks.engineering.model.domain.user;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.UUID;

@ApplicationScoped
public class UserRepositoryBean implements PanacheRepositoryBase<UserEntity, UUID> {
}
