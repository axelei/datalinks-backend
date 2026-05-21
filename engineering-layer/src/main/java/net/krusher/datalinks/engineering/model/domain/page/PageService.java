package net.krusher.datalinks.engineering.model.domain.page;

import io.quarkus.cache.CacheInvalidate;
import io.quarkus.cache.CacheResult;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.domain.model.page.Edit;
import net.krusher.datalinks.domain.model.page.Page;
import net.krusher.datalinks.domain.model.page.PageShort;
import net.krusher.datalinks.domain.model.user.User;
import net.krusher.datalinks.domain.model.user.UserLevel;
import net.krusher.datalinks.engineering.mapper.CategoryMapper;
import net.krusher.datalinks.engineering.mapper.EditMapper;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.mapper.UserMapper;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.engineering.model.domain.upload.UploadUsageEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
@ApplicationScoped
@AllArgsConstructor(onConstructor_ = @Inject)
public class PageService {

    private final EntityManager entityManager;
    private final PageRepositoryBean pageRepositoryBean;
    private final PageMapper pageMapper;
    private final UserMapper userMapper;
    private final EditMapper editMapper;
    private final CategoryMapper categoryMapper;
    private final EditRepositoryBean editRepositoryBean;
    private final UploadService uploadService;
    private final SearchService searchService;

    private static final Pattern UPLOAD_USAGE_PATTERN = Pattern.compile("/file/get/([^\"]*)\"");
    private static final Set<String> ALLOWED_PAGE_COLUMNS = Set.of("creationDate", "modifiedDate", "title", "slug");
    private static final Set<String> ALLOWED_EDIT_COLUMNS = Set.of("date", "ip");


    public Optional<Page> findBySlug(String slug) {
        return pageRepositoryBean.find("slug", slug)
                .firstResultOptional()
                .map(pageEntity -> {
                    Page page = pageMapper.toModel(pageEntity);
                    page.setCreator(userMapper.toModel(pageEntity.getCreator()));
                    page.setCategories(pageEntity.getCategories().stream().map(categoryMapper::toModel).collect(Collectors.toSet()));
                    return page;
                });
    }

    public Optional<PageShort> findShortBySlug(String slug) {
        return pageRepositoryBean.find("slug", slug)
                .firstResultOptional()
                .map(pageMapper::toModelShort);
    }

    @CacheInvalidate(cacheName = "pageCount")
    public void save(Page page, User user, String ip) {
        PageEntity pageEntity = pageMapper.toEntity(page);
        UserEntity userEntity = entityManager.merge(userMapper.toEntity(user));
        pageEntity.setCreator(userEntity);
        pageEntity.setCategories(page.getCategories().stream().map(categoryMapper::toEntity).collect(Collectors.toSet()));
        pageEntity = entityManager.merge(pageEntity);
        processEdit(pageEntity, pageEntity.getCreator(), ip);
        processUploadUsage(pageEntity);
        searchService.indexPage(pageEntity);
    }

    public void updateOrCreate(Page page, User user, String ip) {
        findBySlug(page.getSlug()).ifPresentOrElse(existing -> {
            PageEntity pageEntity = pageMapper.toEntity(existing);
            pageEntity.setTitle(page.getTitle());
            pageEntity.setContent(page.getContent());
            pageEntity.setCategories(page.getCategories().stream().map(categoryMapper::toEntity).collect(Collectors.toSet()));
            pageEntity = entityManager.merge(pageEntity);
            UserEntity userEntity = user != null ? entityManager.merge(userMapper.toEntity(user)) : null;
            processEdit(pageEntity, userEntity, ip);
            processUploadUsage(pageEntity);
            searchService.indexPage(pageEntity);
        }, () -> save(page, user, ip));
    }

    @CacheInvalidate(cacheName = "pageCount")
    public void delete(UUID pageId) {
        deleteEditsForPage(pageId);
        uploadService.deleteUsages(pageId);
        pageRepositoryBean.deleteById(pageId);
    }

    public void deleteEditsForPage(UUID pageId) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaDelete<EditEntity> criteriaDelete = criteriaBuilder.createCriteriaDelete(EditEntity.class);
        Root<EditEntity> root = criteriaDelete.from(EditEntity.class);
        criteriaDelete.where(criteriaBuilder.equal(root.get("page").get("id"), pageId));
        entityManager.createQuery(criteriaDelete).executeUpdate();
    }

    public List<PageShort> pagesSortBy(String column, int page, int pageSize) {
        String safeColumn = ALLOWED_PAGE_COLUMNS.contains(column) ? column : "creationDate";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<PageEntity> pageRoot = cq.from(PageEntity.class);
        Root<UserEntity> userRoot = cq.from(UserEntity.class);

        cq.select(cb.array(pageRoot, userRoot))
                .where(cb.and(
                        cb.equal(pageRoot.get("creator"), userRoot)
                ));

        cq.orderBy(cb.desc(pageRoot.get(safeColumn)));
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

    public void block(String slug, UserLevel readBlock, UserLevel editBlock) {
        findBySlug(slug).ifPresent(page -> {
            page.setReadBlock(readBlock);
            page.setEditBlock(editBlock);
            save(page, page.getCreator(), null);
        });
    }

    public List<Edit> editsSortBy(String column, int page, int pageSize) {
        String safeColumn = ALLOWED_EDIT_COLUMNS.contains(column) ? column : "date";
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

        Root<EditEntity> editRoot = cq.from(EditEntity.class);

        Join<EditEntity, UserEntity> userJoin = editRoot.join("user", JoinType.LEFT);
        Join<EditEntity, PageEntity> pageJoin = editRoot.join("page", JoinType.LEFT);

        cq.select(cb.array(editRoot, userJoin, pageJoin));
        cq.orderBy(cb.desc(editRoot.get(safeColumn)));
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

    @CacheResult(cacheName = "pageCount")
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
        editRepositoryBean.persist(editEntity);
    }

    private void processUploadUsage(PageEntity page) {
        uploadService.deleteUsages(page.getId());
        entityManager.flush();
        entityManager.clear();
        Matcher m = UPLOAD_USAGE_PATTERN.matcher(page.getContent());
        while (m.find()) {
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
        Join<EditEntity, PageEntity> pageJoin = editRoot.join("page", JoinType.LEFT);

        cq.select(cb.array(editRoot, pageJoin))
                .where(cb.equal(editRoot.get("user").get("username"), user.getUsername()));

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
        Optional<EditEntity> editEntity = editRepositoryBean.findByIdOptional(id);
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
