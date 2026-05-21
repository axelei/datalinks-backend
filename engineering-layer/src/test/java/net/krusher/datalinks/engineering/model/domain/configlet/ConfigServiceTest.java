package net.krusher.datalinks.engineering.model.domain.configlet;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.domain.model.configlet.Configlet;
import net.krusher.datalinks.domain.model.configlet.ConfigletKey;
import net.krusher.datalinks.engineering.mapper.ConfigletMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ConfigServiceTest {

    @Test
    void getByKeyReturnsExistingWhenPresent() {
        EntityManager em = Mockito.mock(EntityManager.class);
        ConfigletRepositoryBean repo = Mockito.mock(ConfigletRepositoryBean.class);
        ConfigletMapper mapper = Mockito.mock(ConfigletMapper.class);

        // simulate existing
        ConfigletEntity entity = new ConfigletEntity();
        when(repo.findByIdOptional("READ_LEVEL")).thenReturn(Optional.of(entity));
        Configlet expected = Configlet.of(ConfigletKey.READ_LEVEL, "X");
        when(mapper.toModel(entity)).thenReturn(expected);

        ConfigService service = new ConfigService(em, repo, mapper);
        Configlet res = service.getByKey(ConfigletKey.READ_LEVEL);
        assertEquals(expected, res);
    }

    @Test
    void getByKeyCreatesAndSavesWhenMissing() {
        EntityManager em = Mockito.mock(EntityManager.class);
        ConfigletRepositoryBean repo = Mockito.mock(ConfigletRepositoryBean.class);
        ConfigletMapper mapper = Mockito.mock(ConfigletMapper.class);

        when(repo.findByIdOptional("READ_LEVEL")).thenReturn(Optional.empty());
        ConfigletEntity entity = new ConfigletEntity();
        when(mapper.toEntity(any())).thenReturn(entity);

        ConfigService service = new ConfigService(em, repo, mapper);
        Configlet res = service.getByKey(ConfigletKey.READ_LEVEL);

        // should save via entityManager.merge
        verify(em).merge(entity);
        assertEquals(ConfigletKey.READ_LEVEL, res.getKey());
    }
}
