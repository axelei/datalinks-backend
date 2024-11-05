package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PageService {

    private final EntityManager entityManager;
    private final PageRepositoryBean pageRepositoryBean;
    private final PageMapper pageMapper;

    @Autowired
    public PageService(EntityManager entityManager, PageRepositoryBean pageRepositoryBean, PageMapper pageMapper) {
        this.entityManager = entityManager;
        this.pageRepositoryBean = pageRepositoryBean;
        this.pageMapper = pageMapper;
    }

    public Optional<Page> findBySlug(String slug) {
        Example<PageEntity> example = Example.of(PageEntity.builder().slug(slug).build());
        List<PageEntity> result = pageRepositoryBean.findAll(example);
        return result.stream().findFirst().map(pageMapper::toModel);
    }

    public void save(Page page) {
        PageEntity pageEntity = pageMapper.toEntity(page);
        entityManager.merge(pageEntity);
    }
}
