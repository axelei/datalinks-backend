package net.krusher.datalinks.engineering.model.domain.configlet;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.engineering.mapper.ConfigletMapper;
import net.krusher.datalinks.model.configlet.Configlet;
import net.krusher.datalinks.model.configlet.ConfigletKey;
import org.hibernate.annotations.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ConfigService {

    private final EntityManager entityManager;
    private final ConfigletRepositoryBean configletRepositoryBean;
    private final ConfigletMapper configletMapper;

    @Autowired
    public ConfigService(EntityManager entityManager, ConfigletRepositoryBean configletRepositoryBean, ConfigletMapper configletMapper) {
        this.entityManager = entityManager;
        this.configletRepositoryBean = configletRepositoryBean;
        this.configletMapper = configletMapper;
    }

    @Cacheable("configlets")
    public Configlet getByKey(ConfigletKey configletKey) {
        Optional<Configlet> configlet = getByKeyFromDatabase(configletKey.name());
        if (configlet.isEmpty()) {
            configlet = Optional.of(Configlet.of(configletKey, configletKey.getDefaultValue()));
            save(configlet.get());
        }
        return configlet.get();
    }

    @Cacheable("config")
    public Set<Configlet> getConfig() {
        return Arrays.stream(ConfigletKey.values()).map(this::getByKey).collect(Collectors.toSet());
    }

    private Optional<Configlet> getByKeyFromDatabase(String key) {
        return configletRepositoryBean.findById(key).map(configletMapper::toModel);
    }

    public void save(Configlet configlet) {
        configletRepositoryBean.save(configletMapper.toEntity(configlet));
    }
}
