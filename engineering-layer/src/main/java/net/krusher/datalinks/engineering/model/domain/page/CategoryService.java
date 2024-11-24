package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.CategoryMapper;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.mapper.UserMapper;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.page.PageShort;
import net.krusher.datalinks.model.user.User;
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

    private final CategoryRepositoryBean categoryRepositoryBean;
    private final CategoryMapper categoryMapper;
    private final PageMapper pageMapper;
    private final UserMapper userMapper;
    private final EntityManager entityManager;

    @Autowired
    public CategoryService(CategoryRepositoryBean categoryRepositoryBean,
                           CategoryMapper categoryMapper,
                           PageMapper pageMapper,
                           UserMapper userMapper,
                           EntityManager entityManager) {
        this.categoryRepositoryBean = categoryRepositoryBean;
        this.categoryMapper = categoryMapper;
        this.pageMapper = pageMapper;
        this.userMapper = userMapper;
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

    public void create(Category category) {
        categoryRepositoryBean.save(categoryMapper.toEntity(category));
    }

    public void deleteBySlug(String slug) {
        Optional<Category> category = getCategoryBySlug(slug);
        category.ifPresent((element) -> {
            categoryRepositoryBean.deleteById(element.getId());
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

        Root<PageEntity> pageRoot = cq.from(PageEntity.class);

        Join<PageEntity, UserEntity> userJoin = pageRoot.join("creator", JoinType.LEFT);
        Join<CategoryEntity, PageEntity> categoryJoin = pageRoot.join("categories", JoinType.LEFT);

        cq.select(cb.array(pageRoot, categoryJoin, userJoin))
                .where(
                        cb.equal(categoryJoin.get("slug"), categorySlug)
                );

        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream()
                .map(result -> {
                    PageShort resultPage = pageMapper.toModelShort((PageEntity) result[0]);
                    resultPage.setCreator(userMapper.toModel((UserEntity) result[2]));
                    return resultPage;
                })
                .collect(Collectors.toList());
    }

}
