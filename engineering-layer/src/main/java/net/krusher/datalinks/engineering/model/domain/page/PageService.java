package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.mapper.UserMapper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.engineering.model.domain.upload.UploadUsageEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.model.page.Page;
import net.krusher.datalinks.model.page.PageShort;
import net.krusher.datalinks.model.upload.Upload;
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

@Service
public class PageService {

    private final EntityManager entityManager;
    private final PageRepositoryBean pageRepositoryBean;
    private final PageMapper pageMapper;
    private final UserMapper userMapper;
    private final EditRepositoryBean editRepositoryBean;
    private final UploadService uploadService;

    private static final Pattern UPLOAD_USAGE_PATTERN = Pattern.compile("/file/get/([^\"]*)\"");

    @Autowired
    public PageService(EntityManager entityManager,
                       PageRepositoryBean pageRepositoryBean,
                       PageMapper pageMapper,
                       UserMapper userMapper,
                       EditRepositoryBean editRepositoryBean,
                       UploadService uploadService) {
        this.entityManager = entityManager;
        this.pageRepositoryBean = pageRepositoryBean;
        this.pageMapper = pageMapper;
        this.userMapper = userMapper;
        this.editRepositoryBean = editRepositoryBean;
        this.uploadService = uploadService;
    }

    public Optional<Page> findBySlug(String slug) {
        Example<PageEntity> example = Example.of(PageEntity.builder().slug(slug).build());
        List<PageEntity> result = pageRepositoryBean.findAll(example);
        return result.stream().findFirst().map(pageMapper::toModel);
    }

    public void save(Page page, User user, String ip) {
        PageEntity pageEntity = pageMapper.toEntity(page);
        pageEntity = entityManager.merge(pageEntity);
        processEdit(pageEntity, userMapper.toEntity(user), ip);
        processUploadUsage(page);
    }

    public void delete(UUID pageId) {
        deleteEditsForPage(pageId);
        uploadService.deleteUsages(pageId);
        pageRepositoryBean.deleteById(pageId);
    }

    public void deleteEditsForPage(UUID pageId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EditEntity> cq = cb.createQuery(EditEntity.class);
        Root<EditEntity> from = cq.from(EditEntity.class);
        cq.where(cb.equal(from.get("pageId"), pageId));
        TypedQuery<EditEntity> query = entityManager.createQuery(cq);
        editRepositoryBean.deleteAll(query.getResultList());
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

    private void processEdit(PageEntity pageEntity, UserEntity userEntity, String ip) {
        EditEntity editEntity = EditEntity.builder()
                .content(pageEntity.getContent())
                .pageId(pageEntity.getId())
                .userId(Optional.ofNullable(userEntity).map(UserEntity::getId).orElse(null))
                .ip(ip)
                .build();
        editRepositoryBean.save(editEntity);
    }

    private void processUploadUsage(Page page) {
        uploadService.deleteUsages(page.getId());
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

}
