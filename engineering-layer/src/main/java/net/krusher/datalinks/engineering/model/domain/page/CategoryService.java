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
import net.krusher.datalinks.model.page.PageShort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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

    public void processLinks(PageEntity page, Set<Category> categories) {
        deleteLinksByPage(page.getId());
        entityManager.flush();
        entityManager.clear();
        for (Category category : categories) {
            getCategoryBySlug(category.getSlug()).ifPresent((element) -> {
                entityManager.merge(CategoryLinkEntity.builder()
                        .id(CategoryLinkEntityKey.builder()
                                .categoryId(element.getId())
                                .pageId(page.getId())
                                .build())
                        .build());
            });
        }
    }

    public void deleteLinksByPage(UUID pageId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<CategoryLinkEntity> delete = cb.createCriteriaDelete(CategoryLinkEntity.class);
        Root<CategoryLinkEntity> root = delete.from(CategoryLinkEntity.class);
        delete.where(cb.equal(root.get("id").get("pageId"), pageId));
        entityManager.createQuery(delete).executeUpdate();
    }

    public void deleteLinksByCategory(UUID categoryId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<CategoryLinkEntity> delete = cb.createCriteriaDelete(CategoryLinkEntity.class);
        Root<CategoryLinkEntity> root = delete.from(CategoryLinkEntity.class);
        delete.where(cb.equal(root.get("id").get("categoryId"), categoryId));
        entityManager.createQuery(delete).executeUpdate();
    }

    public void create(Category category) {
        categoryRepositoryBean.save(categoryMapper.toEntity(category));
    }

    public void deleteBySlug(String slug) {
        Optional<Category> category = getCategoryBySlug(slug);
        category.ifPresent((element) -> {
            categoryRepositoryBean.deleteById(element.getId());
            deleteLinksByCategory(element.getId());
        });
    }

    public Optional<Category> getCategoryBySlug(String slug) {
        return categoryRepositoryBean.findAll(Example.of(CategoryEntity.builder().slug(slug).build()))
                .stream().findFirst()
                .map(categoryMapper::toModel);
    }

    public List<PageShort> getPagesByCategorySlug(String categorySlug, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<CategoryLinkEntity> categoryLinkEntityRoot = cq.from(CategoryLinkEntity.class);
        Root<CategoryEntity> categoryEntityRoot = cq.from(CategoryEntity.class);
        Root<PageEntity> pageEntityRoot = cq.from(PageEntity.class);
        Root<UserEntity> userEntityRoot = cq.from(UserEntity.class);

        cq.select(cb.array(categoryLinkEntityRoot, categoryEntityRoot, pageEntityRoot, userEntityRoot))
                .where(cb.and(
                        cb.equal(categoryLinkEntityRoot.get("id").get("categoryId"), categoryEntityRoot.get("id")),
                        cb.equal(categoryLinkEntityRoot.get("id").get("pageId"), pageEntityRoot.get("id")),
                        cb.equal(pageEntityRoot.get("creatorId"), userEntityRoot.get("id")),
                        cb.equal(categoryEntityRoot.get("slug"), categorySlug)
                ));

        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream()
                .map(result -> {
                    PageShort resultPage = pageMapper.toModelShort((PageEntity) result[2]);
                    resultPage.setCreatorName(((UserEntity) result[3]).getUsername());
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
                        cb.equal(categoryLinkEntityRoot.get("id").get("categoryId"), categoryEntityRoot.get("id")),
                        cb.equal(categoryLinkEntityRoot.get("id").get("pageId"), pageId)
                ));

        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query.getResultList();

        return results.stream()
                .map(result -> categoryMapper.toModel((CategoryEntity) result[1]))
                .collect(Collectors.toSet());
    }
}
