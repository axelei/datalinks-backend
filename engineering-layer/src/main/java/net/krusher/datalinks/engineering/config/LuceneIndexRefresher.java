package net.krusher.datalinks.engineering.config;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;
import net.krusher.datalinks.engineering.model.domain.page.CategoryEntity;
import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.engineering.model.domain.upload.UploadEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
@JBossLog
@lombok.AllArgsConstructor(onConstructor_ = @Inject)
public class LuceneIndexRefresher {

    private final EntityManager entityManager;
    private final LuceneIndexManager indexManager;
    private final SearchService searchService;


    @Transactional
    void onStart(@Observes StartupEvent ev) {
        try {
            indexManager.deleteAll();
            indexPages();
            indexCategories();
            indexUsers();
            indexUploads();
            indexManager.commit();
            log.info("Lucene indexes refreshed");
        } catch (IOException e) {
            log.error("Error generating Lucene indexes", e);
        }
    }

    private void indexPages() throws IOException {
        List<PageEntity> entities = entityManager
                .createQuery("SELECT p FROM PageEntity p", PageEntity.class)
                .getResultList();
        for (PageEntity e : entities) {
            searchService.indexPage(e);
        }
    }

    private void indexCategories() throws IOException {
        List<CategoryEntity> entities = entityManager
                .createQuery("SELECT c FROM CategoryEntity c", CategoryEntity.class)
                .getResultList();
        for (CategoryEntity e : entities) {
            searchService.indexCategory(e);
        }
    }

    private void indexUsers() throws IOException {
        List<UserEntity> entities = entityManager
                .createQuery("SELECT u FROM UserEntity u", UserEntity.class)
                .getResultList();
        for (UserEntity e : entities) {
            searchService.indexUser(e);
        }
    }

    private void indexUploads() throws IOException {
        List<UploadEntity> entities = entityManager
                .createQuery("SELECT u FROM UploadEntity u", UploadEntity.class)
                .getResultList();
        for (UploadEntity e : entities) {
            searchService.indexUpload(e);
        }
    }
}
