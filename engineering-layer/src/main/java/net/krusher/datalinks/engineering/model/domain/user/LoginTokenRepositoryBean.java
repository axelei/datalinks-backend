package net.krusher.datalinks.engineering.model.domain.user;

import net.krusher.datalinks.engineering.model.domain.page.LoginTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoginTokenRepositoryBean extends JpaRepository<LoginTokenEntity, UUID> {
}
