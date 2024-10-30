package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.model.page.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public Page findByTitle(String title) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PageEntity> cq = cb.createQuery(PageEntity.class);

        Root<PageEntity> book = cq.from(PageEntity.class);
        Predicate titlePredicate = cb.equal(book.get("title"), title);
        cq.where(titlePredicate);

        TypedQuery<PageEntity> query = entityManager.createQuery(cq);

        return pageMapper.toModel(query.getResultList().stream().findFirst().orElse(null));
    }

    public void save(Page page) {
        PageEntity pageEntity = pageMapper.toEntity(page);
        entityManager.merge(pageEntity);
    }
}
