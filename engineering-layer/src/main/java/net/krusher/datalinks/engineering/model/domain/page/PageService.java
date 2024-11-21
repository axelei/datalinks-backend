package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.EditMapper;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.mapper.UserMapper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.engineering.model.domain.upload.UploadUsageEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserRepositoryBean;
import net.krusher.datalinks.model.page.Edit;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PageService {

    private final EntityManager entityManager;
    private final PageRepositoryBean pageRepositoryBean;
    private final PageMapper pageMapper;
    private final UserMapper userMapper;
    private final EditMapper editMapper;
    private final CategoryService categoryService;
    private final EditRepositoryBean editRepositoryBean;
    private final UploadService uploadService;

    private static final Pattern UPLOAD_USAGE_PATTERN = Pattern.compile("/file/get/([^\"]*)\"");
    private final UserRepositoryBean userRepositoryBean;

    @Autowired
    public PageService(EntityManager entityManager,
                       PageRepositoryBean pageRepositoryBean,
                       PageMapper pageMapper,
                       UserMapper userMapper,
                       EditMapper editMapper,
                       CategoryService categoryService,
                       EditRepositoryBean editRepositoryBean,
                       UploadService uploadService, UserRepositoryBean userRepositoryBean) {
        this.entityManager = entityManager;
        this.pageRepositoryBean = pageRepositoryBean;
        this.pageMapper = pageMapper;
        this.userMapper = userMapper;
        this.editRepositoryBean = editRepositoryBean;
        this.editMapper = editMapper;
        this.categoryService = categoryService;
        this.uploadService = uploadService;
        this.userRepositoryBean = userRepositoryBean;
    }

    public Optional<Page> findBySlug(String slug) {
        return pageRepositoryBean.findAll(Example.of(PageEntity.builder().slug(slug).build()))
                .stream()
                .findFirst()
                .map(pageEntity -> {
                    Page page = pageMapper.toModel(pageEntity);
                    page.setCategories(categoryService.findByPage(page.getId()));
                    return page;
                });
    }

    public void save(Page page, User user, String ip) {
        PageEntity pageEntity = pageMapper.toEntity(page);
        pageEntity = entityManager.merge(pageEntity);
        processEdit(pageEntity, userMapper.toEntity(user), ip);
        categoryService.processLinks(pageEntity, page.getCategories());
        processUploadUsage(pageEntity);
    }

    public void delete(UUID pageId) {
        deleteEditsForPage(pageId);
        uploadService.deleteUsages(pageId);
        pageRepositoryBean.deleteById(pageId);
        categoryService.deleteLinksByPage(pageId);
    }

    public void deleteEditsForPage(UUID pageId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<EditEntity> criteriaDelete = criteriaBuilder.createCriteriaDelete(EditEntity.class);
        Root<EditEntity> root = criteriaDelete.from(EditEntity.class);
        criteriaDelete.where(criteriaBuilder.equal(root.get("pageId"), pageId));
        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    public List<PageShort> pagesSortBy(String column, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<PageEntity> pageRoot = cq.from(PageEntity.class);
        Root<UserEntity> userRoot = cq.from(UserEntity.class);

        cq.select(cb.array(pageRoot, userRoot))
                .where(cb.and(
                        cb.equal(pageRoot.get("creatorId"), userRoot.get("id"))
                ));

        cq.orderBy(cb.desc(pageRoot.get(column)));
        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream().map(result -> {
            PageShort pageItem = pageMapper.toModelShort((PageEntity) result[0]);
            pageItem.setCreatorName(((UserEntity) result[1]).getUsername());
            return pageItem;
        }).collect(Collectors.toList());
    }

    public List<Edit> editsSortBy(String column, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<EditEntity> editRoot = cq.from(EditEntity.class);
        Root<PageEntity> pageRoot = cq.from(PageEntity.class);
        Root<UserEntity> userRoot = cq.from(UserEntity.class);

        cq.select(cb.array(editRoot, pageRoot, userRoot))
                .where(cb.and(
                        cb.equal(editRoot.get("pageId"), pageRoot.get("id")),
                        cb.equal(editRoot.get("userId"), userRoot.get("id"))
                ));

        cq.orderBy(cb.desc(editRoot.get(column)));
        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream().map(result -> {
            Edit edit = editMapper.toModel((EditEntity) result[0]);
            edit.setTitle(((PageEntity) result[1]).getTitle());
            edit.setUsername(((UserEntity) result[2]).getUsername());
            return edit;
        }).collect(Collectors.toList());
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

    @Cacheable("pageCount")
    public int count() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(PageEntity.class)));
        return entityManager.createQuery(cq).getSingleResult().intValue();
    }

    private void processEdit(PageEntity pageEntity, UserEntity userEntity, String ip) {
        EditEntity editEntity = EditEntity.builder()
                .content(pageEntity.getContent())
                .pageId(pageEntity.getId())
                .userId(Optional.ofNullable(userEntity).map(UserEntity::getId).orElse(null))
                .ip(ip)
                .build();
        editRepositoryBean.save(editEntity);
    }

    private void processUploadUsage(PageEntity page) {
        uploadService.deleteUsages(page.getId());
        entityManager.flush();
        entityManager.clear();
        Matcher m = UPLOAD_USAGE_PATTERN.matcher(page.getContent());
        while(m.find()) {
            String slug = m.group(1);
            uploadService.findBySlug(slug).ifPresent(upload -> {
                UploadUsageEntity usage = UploadUsageEntity.builder()
                        .pageId(page.getId())
                        .uploadId(upload.getId())
                        .build();
                uploadService.saveUsage(usage);
            });
        }
    }

    public List<Edit> findByUser(User user, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<EditEntity> editRoot = cq.from(EditEntity.class);
        Root<PageEntity> pageRoot = cq.from(PageEntity.class);

        cq.select(cb.array(editRoot, pageRoot))
                .where(cb.and(
                        cb.equal(editRoot.get("pageId"), pageRoot.get("id")),
                        cb.equal(editRoot.get("userId"), user.getId())
                                ));

        cq.orderBy(cb.desc(editRoot.get("date")));
        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream().map(result -> {
            Edit edit = editMapper.toModel((EditEntity) result[0]);
            edit.setTitle(((PageEntity) result[1]).getTitle());
            return edit;
        }).collect(Collectors.toList());
    }

    public List<Edit> findByPage(Page page, int pageNumber, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<EditEntity> editFrom = cq.from(EditEntity.class);
        Root<UserEntity> userFrom = cq.from(UserEntity.class);

        cq.select(cb.array(editFrom, userFrom))
                .where(cb.and(
                        cb.equal(editFrom.get("userId"), userFrom.get("id")),
                        cb.equal(editFrom.get("pageId"), page.getId())
                ));

        cq.orderBy(cb.desc(editFrom.get("date")));
        TypedQuery<Object[]> query = entityManager.createQuery(cq);

        List<Object[]> results = query
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream().map(result -> {
            Edit edit = editMapper.toModel((EditEntity) result[0]);
            edit.setUsername(((UserEntity) result[1]).getUsername());
            return edit;
        }).collect(Collectors.toList());
    }

    public Optional<Edit> findEditById(UUID id) {
       Optional<Edit> edit = editRepositoryBean.findById(id).map(editMapper::toModel);
       if (edit.isEmpty()) {
           return Optional.empty();
       }
       Optional<User> user = userRepositoryBean.findById(edit.get().getUserId()).map(userMapper::toModel);
       user.ifPresent(value -> edit.get().setUsername(value.getUsername()));
       Optional<Page> page = pageRepositoryBean.findById(edit.get().getPageId()).map(pageMapper::toModel);
       page.ifPresent(value -> edit.get().setTitle(value.getTitle()));
       return edit;
    }

}
