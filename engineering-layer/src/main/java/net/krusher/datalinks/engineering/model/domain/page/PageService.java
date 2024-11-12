package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.page.PageShort;
import net.krusher.datalinks.model.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PageService {

    private final EntityManager entityManager;
    private final PageRepositoryBean pageRepositoryBean;
    private final PageMapper pageMapper;
    private final EditRepositoryBean editRepositoryBean;

    @Autowired
    public PageService(EntityManager entityManager, PageRepositoryBean pageRepositoryBean, PageMapper pageMapper, EditRepositoryBean editRepositoryBean) {
        this.entityManager = entityManager;
        this.pageRepositoryBean = pageRepositoryBean;
        this.pageMapper = pageMapper;
        this.editRepositoryBean = editRepositoryBean;
    }

    public Optional<Page> findBySlug(String slug) {
        Example<PageEntity> example = Example.of(PageEntity.builder().slug(slug).build());
        List<PageEntity> result = pageRepositoryBean.findAll(example);
        return result.stream().findFirst().map(pageMapper::toModel);
    }

    public void save(Page page, User user) {
        PageEntity pageEntity = pageMapper.toEntity(page);
        entityManager.merge(pageEntity);
        EditEntity editEntity = EditEntity.builder()
                .content(page.getContent())
                .userId(Optional.ofNullable(user).map(User::getId).orElse(null))
                .build();
        editRepositoryBean.save(editEntity);
    }

    public List<PageShort> pagesSortBy(String column, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageEntity> cq = cb.createQuery(PageEntity.class);
        cq.orderBy(cb.desc(cq.from(PageEntity.class).get(column)));
        TypedQuery<PageEntity> query = entityManager.createQuery(cq);
        return query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList().stream().map(pageMapper::toModelShort).toList();
    }

    public List<PageShort> contributions(UUID userId, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageEntity> cq = cb.createQuery(PageEntity.class);
        Root<PageEntity> from = cq.from(PageEntity.class);
        cq.where(cb.equal(from.get("creatorId"), userId));
        TypedQuery<PageEntity> query = entityManager.createQuery(cq);
        return query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList().stream().map(pageMapper::toModelShort).toList();
    }

    public List<PageShort> allPages(int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageEntity> cq = cb.createQuery(PageEntity.class);
        cq.from(PageEntity.class);
        TypedQuery<PageEntity> query = entityManager.createQuery(cq);
        return query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList().stream().map(pageMapper::toModelShort).toList();
    }

    public List<PageShort> titleSearch(String query) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageEntity> cq = cb.createQuery(PageEntity.class);
        cq.where(cb.like(cb.lower(cq.from(PageEntity.class).get("title")), "%" + query.toLowerCase() + "%"));
        TypedQuery<PageEntity> typedQuery = entityManager.createQuery(cq);
        return typedQuery
                .setMaxResults(10)
                .getResultList().stream().map(pageMapper::toModelShort).toList();
    }

    public List<PageShort> search(String query, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageEntity> cq = cb.createQuery(PageEntity.class);
        cq.where(cb.like(cb.lower(cq.from(PageEntity.class).get("title")), "%" + query.toLowerCase() + "%"));
        TypedQuery<PageEntity> typedQuery = entityManager.createQuery(cq);
        return typedQuery
                .setMaxResults(10)
                .getResultList().stream().map(pageMapper::toModelShort).toList();
    }

    @Cacheable("pageCount")
    public int count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(PageEntity.class)));
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }
}
