package net.krusher.datalinks.engineering.model.domain.page;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PageRepositoryBean extends JpaRepository<PageEntity, UUID> {


}
