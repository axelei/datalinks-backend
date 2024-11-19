package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.CategoryMapper;
import net.krusher.datalinks.model.page.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CategoryService {

    private final CategoryLinkRepositoryBean categoryLinkRepositoryBean;
    private final CategoryRepositoryBean categoryRepositoryBean;
    private final CategoryMapper categoryMapper;
    private final EntityManager entityManager;

    @Autowired
    public CategoryService(CategoryLinkRepositoryBean categoryLinkRepositoryBean,
                           CategoryRepositoryBean categoryRepositoryBean,
                           CategoryMapper categoryMapper,
                           EntityManager entityManager) {
        this.categoryLinkRepositoryBean = categoryLinkRepositoryBean;
        this.categoryRepositoryBean = categoryRepositoryBean;
        this.categoryMapper = categoryMapper;
        this.entityManager = entityManager;
    }

    public List<Category> allCategories(int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CategoryEntity> cq = cb.createQuery(CategoryEntity.class);
        cq.orderBy(cb.desc(cq.from(CategoryEntity.class).get("name")));
        TypedQuery<CategoryEntity> query = entityManager.createQuery(cq);
        return query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList().stream().map(categoryMapper::toModel).toList();
    }

    public void create(String name) {
        categoryRepositoryBean.save(CategoryEntity.builder().name(name).build());
    }

    public void delete(String name) {
        categoryRepositoryBean.deleteById(name);
        deleteLinks(name);
    }

    private void deleteLinks(String name) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<CategoryLinkEntity> criteriaDelete = criteriaBuilder.createCriteriaDelete(CategoryLinkEntity.class);
        Root<CategoryLinkEntity> root = criteriaDelete.from(CategoryLinkEntity.class);
        criteriaDelete.where(criteriaBuilder.equal(root.get("id").get("name"), name));
        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    public Optional<Category> getCategory(String name) {
        return categoryRepositoryBean.findById(name).map(categoryMapper::toModel);
    }

    public Set<Category> findByPage(UUID pageId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<CategoryLinkEntity> categoryLinkEntityRoot = cq.from(CategoryLinkEntity.class);
        Root<CategoryEntity> categoryEntityRoot = cq.from(CategoryEntity.class);

        cq.select(cb.array(categoryLinkEntityRoot, categoryEntityRoot))
                .where(cb.and(
                        cb.equal(categoryLinkEntityRoot.get("id").get("name"), categoryEntityRoot.get("name")),
                        cb.equal(categoryLinkEntityRoot.get("id").get("pageId"), pageId)
                ));

        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> categoryMapper.toModel((CategoryEntity) result[1]))
                .collect(Collectors.toSet());
    }



}
