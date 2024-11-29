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
import net.krusher.datalinks.engineering.mapper.EditMapper;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.mapper.UserMapper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.engineering.model.domain.upload.UploadUsageEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
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
    private final CategoryMapper categoryMapper;
    private final EditRepositoryBean editRepositoryBean;
    private final UploadService uploadService;

    private static final Pattern UPLOAD_USAGE_PATTERN = Pattern.compile("/file/get/([^\"]*)\"");

    @Autowired
    public PageService(EntityManager entityManager,
                       PageRepositoryBean pageRepositoryBean,
                       PageMapper pageMapper,
                       UserMapper userMapper,
                       EditMapper editMapper,
                       CategoryMapper categoryMapper,
                       EditRepositoryBean editRepositoryBean,
                       UploadService uploadService) {
        this.entityManager = entityManager;
        this.pageRepositoryBean = pageRepositoryBean;
        this.pageMapper = pageMapper;
        this.userMapper = userMapper;
        this.editRepositoryBean = editRepositoryBean;
        this.categoryMapper = categoryMapper;
        this.editMapper = editMapper;
        this.uploadService = uploadService;
    }

    public Optional<Page> findBySlug(String slug) {
        return pageRepositoryBean.findAll(Example.of(PageEntity.builder().slug(slug).build()))
                .stream()
                .findFirst()
                .map(pageEntity -> {
                    Page page = pageMapper.toModel(pageEntity);
                    page.setCategories(pageEntity.getCategories().stream().map(categoryMapper::toModel).collect(Collectors.toSet()));
                    return page;
                });
    }

    public void save(Page page, User user, String ip) {
        PageEntity pageEntity = pageMapper.toEntity(page);
        pageEntity.setCreator(userMapper.toEntity(user));
        pageEntity.setCategories(page.getCategories().stream().map(categoryMapper::toEntity).collect(Collectors.toSet()));
        pageEntity = entityManager.merge(pageEntity);
        processEdit(pageEntity, userMapper.toEntity(user), ip);
        processUploadUsage(pageEntity);
    }

    public void updateOrCreate(Page page, User user, String ip) {
        findBySlug(page.getSlug()).ifPresentOrElse(existing -> {
            PageEntity pageEntity = pageMapper.toEntity(existing);
            pageEntity.setContent(page.getContent());
            pageEntity.setCategories(page.getCategories().stream().map(categoryMapper::toEntity).collect(Collectors.toSet()));
            pageEntity = entityManager.merge(pageEntity);
            processEdit(pageEntity, userMapper.toEntity(user), ip);
            processUploadUsage(pageEntity);
        }, () -> save(page, user, ip));
    }

    public void delete(UUID pageId) {
        deleteEditsForPage(pageId);
        uploadService.deleteUsages(pageId);
        pageRepositoryBean.deleteById(pageId);
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
                        cb.equal(pageRoot.get("creator"), userRoot)
                ));

        cq.orderBy(cb.desc(pageRoot.get(column)));
        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream().map(result -> {
            PageShort pageItem = pageMapper.toModelShort((PageEntity) result[0]);
            pageItem.setCreator(userMapper.toModel((UserEntity) result[1]));
            return pageItem;
        }).collect(Collectors.toList());
    }

    public List<Edit> editsSortBy(String column, int page, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<EditEntity> editRoot = cq.from(EditEntity.class);

        Join<EditEntity, UserEntity> userJoin = editRoot.join("user", JoinType.LEFT);
        Join<EditEntity, PageEntity> pageJoin = editRoot.join("page", JoinType.LEFT);

        cq.select(cb.array(editRoot, userJoin, pageJoin));
        cq.orderBy(cb.desc(editRoot.get(column)));
        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream().map(result -> {
            Edit edit = editMapper.toModel((EditEntity) result[0]);
            edit.setUser(userMapper.toModel((UserEntity) result[1]));
            edit.setPage(pageMapper.toModelShort((PageEntity) result[2]));
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
                .page(pageEntity)
                .user(userEntity)
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
                        cb.equal(editRoot.get("page"), pageRoot),
                        cb.equal(editRoot.get("user"), userMapper.toEntity(user))
                                ));

        cq.orderBy(cb.desc(editRoot.get("date")));
        TypedQuery<Object[]> query = entityManager.createQuery(cq);
        List<Object[]> results = query
                .setFirstResult(page * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream().map(result -> {
            Edit edit = editMapper.toModel((EditEntity) result[0]);
            edit.setPage(pageMapper.toModelShort((PageEntity) result[1]));
            return edit;
        }).collect(Collectors.toList());
    }

    public List<Edit> findByPage(Page page, int pageNumber, int pageSize) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<EditEntity> editFrom = cq.from(EditEntity.class);

        Join<EditEntity, UserEntity> userJoin = editFrom.join("user", JoinType.LEFT);

        cq.select(cb.array(editFrom, userJoin))
                .where(cb.equal(editFrom.get("page"), pageMapper.toEntity(page)))
                .orderBy(cb.desc(editFrom.get("date")));

        TypedQuery<Object[]> query = entityManager.createQuery(cq);

        List<Object[]> results = query
                .setFirstResult(pageNumber * pageSize)
                .setMaxResults(pageSize)
                .getResultList();

        return results.stream().map(result -> {
            Edit edit = editMapper.toModel((EditEntity) result[0]);
            edit.setUser(userMapper.toModel((UserEntity) result[1]));
            return edit;
        }).collect(Collectors.toList());
    }

    public Optional<Edit> findEditById(UUID id) {
       Optional<EditEntity> editEntity = editRepositoryBean.findById(id);
       if (editEntity.isEmpty()) {
           return Optional.empty();
       }
       Edit edit = editMapper.toModel(editEntity.get());
       edit.setUser(userMapper.toModel(editEntity.get().getUser()));
       edit.setPage(pageMapper.toModelShort(editEntity.get().getPage()));
       return Optional.of(edit);
    }

    public List<String> findAllTitles() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        cq.select((cq.from(PageEntity.class).get("title")));
        return entityManager.createQuery(cq).getResultList();
    }

}
