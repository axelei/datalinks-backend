package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.CategoryMapper;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.page.PageShort;
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
    private final PageMapper pageMapper;
    private final EntityManager entityManager;

    @Autowired
    public CategoryService(CategoryLinkRepositoryBean categoryLinkRepositoryBean,
                           CategoryRepositoryBean categoryRepositoryBean,
                           CategoryMapper categoryMapper,
                           PageMapper pageMapper,
                           EntityManager entityManager) {
        this.categoryLinkRepositoryBean = categoryLinkRepositoryBean;
        this.categoryRepositoryBean = categoryRepositoryBean;
        this.categoryMapper = categoryMapper;
        this.pageMapper = pageMapper;
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

    public void processLinks(Page page, Set<Category> categories) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<CategoryLinkEntity> delete = cb. createCriteriaDelete(CategoryLinkEntity.class);
        Root<CategoryLinkEntity> e = delete.from(CategoryLinkEntity.class);
        delete.where(cb.equal(e.get("id").get("pageId"), page.getId()));
        entityManager.createQuery(delete).executeUpdate();

        for (Category category : categories) {
            categoryLinkRepositoryBean.save(CategoryLinkEntity.builder()
                            .id(CategoryLinkEntityKey.builder()
                                    .name(category.getName())
                                    .pageId(page.getId())
                                    .build())
                    .build());
        }
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

    public List<PageShort> getPagesByCategory(String categoryName, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<CategoryLinkEntity> categoryLinkEntityRoot = cq.from(CategoryLinkEntity.class);
        Root<PageEntity> pageEntityRoot = cq.from(PageEntity.class);
        Root<UserEntity> userEntityRoot = cq.from(UserEntity.class);

        cq.select(cb.array(categoryLinkEntityRoot, pageEntityRoot, userEntityRoot))
                .where(cb.and(
                        cb.equal(categoryLinkEntityRoot.get("id").get("name"), categoryName),
                        cb.equal(categoryLinkEntityRoot.get("id").get("pageId"), pageEntityRoot.get("id")),
                        cb.equal(pageEntityRoot.get("creatorId"), userEntityRoot.get("id"))
                ));

        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream()
                .map(result -> {
                    PageShort resultPage = pageMapper.toModelShort((PageEntity) result[1]);
                    resultPage.setCreatorName(((UserEntity) result[2]).getUsername());
                    return resultPage;
                })
                .collect(Collectors.toList());
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
