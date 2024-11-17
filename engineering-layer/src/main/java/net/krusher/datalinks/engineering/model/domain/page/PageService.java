package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
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
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
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
                       EditRepositoryBean editRepositoryBean,
                       UploadService uploadService, UserRepositoryBean userRepositoryBean) {
        this.entityManager = entityManager;
        this.pageRepositoryBean = pageRepositoryBean;
        this.pageMapper = pageMapper;
        this.userMapper = userMapper;
        this.editRepositoryBean = editRepositoryBean;
        this.editMapper = editMapper;
        this.uploadService = uploadService;
        this.userRepositoryBean = userRepositoryBean;
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
