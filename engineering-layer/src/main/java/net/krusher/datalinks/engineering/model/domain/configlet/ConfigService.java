package net.krusher.datalinks.engineering.model.domain.configlet;

import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import net.krusher.datalinks.engineering.mapper.ConfigletMapper;
import net.krusher.datalinks.domain.model.configlet.Configlet;
import net.krusher.datalinks.domain.model.configlet.ConfigletKey;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConfigService {

    private final EntityManager entityManager;
    private final ConfigletRepositoryBean configletRepositoryBean;
    private final ConfigletMapper configletMapper;

    @Inject
    public ConfigService(EntityManager entityManager, ConfigletRepositoryBean configletRepositoryBean, ConfigletMapper configletMapper) {
        this.entityManager = entityManager;
        this.configletRepositoryBean = configletRepositoryBean;
        this.configletMapper = configletMapper;
    }

    @CacheResult(cacheName = "configlets")
    @Transactional
    public Configlet getByKey(ConfigletKey configletKey) {
        Optional<Configlet> configlet = getByKeyFromDatabase(configletKey.name());
        if (configlet.isEmpty()) {
            configlet = Optional.of(Configlet.of(configletKey, configletKey.getDefaultValue()));
            save(configlet.get());
        }
        return configlet.get();
    }

    @CacheResult(cacheName = "config")
    public Set<Configlet> getConfig() {
        return Arrays.stream(ConfigletKey.values()).map(this::getByKey).collect(Collectors.toSet());
    }

    private Optional<Configlet> getByKeyFromDatabase(String key) {
        return configletRepositoryBean.findByIdOptional(key).map(configletMapper::toModel);
    }

    @Transactional
    public void save(Configlet configlet) {
        entityManager.merge(configletMapper.toEntity(configlet));
    }
}
