package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
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

    public List<Page> newPages(int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageEntity> cq = cb.createQuery(PageEntity.class);
        cq.orderBy(cb.desc(cq.from(PageEntity.class).get("creationDate")));
        TypedQuery<PageEntity> query = entityManager.createQuery(cq);
        return query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList().stream().map(pageMapper::toModel).toList();
    }
}
