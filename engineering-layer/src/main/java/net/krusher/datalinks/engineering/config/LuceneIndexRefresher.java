package net.krusher.datalinks.engineering.config;

import io.quarkus.runtime.LaunchMode;
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
import net.krusher.datalinks.model.search.Foundling;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.io.IOException;
import java.util.List;

@ApplicationScoped
@JBossLog
public class LuceneIndexRefresher {

    private final EntityManager entityManager;
    private final LuceneIndexManager indexManager;

    @Inject
    public LuceneIndexRefresher(EntityManager entityManager, LuceneIndexManager indexManager) {
        this.entityManager = entityManager;
        this.indexManager = indexManager;
    }

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if (LaunchMode.current() != LaunchMode.DEVELOPMENT) {
            return;
        }
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
            String content = StringUtils.defaultString(e.getContent());
            Document doc = new Document();
            doc.add(new StringField("doc_id", docId(Foundling.FoundlingType.PAGE, e.getId().toString()), Field.Store.NO));
            doc.add(new StringField("type", Foundling.FoundlingType.PAGE.name(), Field.Store.YES));
            doc.add(new StoredField("id", e.getId().toString()));
            doc.add(new TextField("title", StringUtils.defaultString(e.getTitle()), Field.Store.NO));
            doc.add(new TextField("content", content, Field.Store.NO));
            doc.add(new StoredField("foundling_title", StringUtils.defaultString(e.getTitle())));
            doc.add(new StoredField("foundling_content", e.summarize(content)));
            indexManager.addOrUpdate(docId(Foundling.FoundlingType.PAGE, e.getId().toString()), doc);
        }
    }

    private void indexCategories() throws IOException {
        List<CategoryEntity> entities = entityManager
                .createQuery("SELECT c FROM CategoryEntity c", CategoryEntity.class)
                .getResultList();
        for (CategoryEntity e : entities) {
            Document doc = new Document();
            doc.add(new StringField("doc_id", docId(Foundling.FoundlingType.CATEGORY, e.getId().toString()), Field.Store.NO));
            doc.add(new StringField("type", Foundling.FoundlingType.CATEGORY.name(), Field.Store.YES));
            doc.add(new StoredField("id", e.getId().toString()));
            doc.add(new TextField("name", StringUtils.defaultString(e.getName()), Field.Store.NO));
            doc.add(new StoredField("foundling_title", StringUtils.defaultString(e.getName())));
            indexManager.addOrUpdate(docId(Foundling.FoundlingType.CATEGORY, e.getId().toString()), doc);
        }
    }

    private void indexUsers() throws IOException {
        List<UserEntity> entities = entityManager
                .createQuery("SELECT u FROM UserEntity u", UserEntity.class)
                .getResultList();
        for (UserEntity e : entities) {
            Document doc = new Document();
            doc.add(new StringField("doc_id", docId(Foundling.FoundlingType.USER, e.getId().toString()), Field.Store.NO));
            doc.add(new StringField("type", Foundling.FoundlingType.USER.name(), Field.Store.YES));
            doc.add(new StoredField("id", e.getId().toString()));
            doc.add(new TextField("username", StringUtils.defaultString(e.getUsername()), Field.Store.NO));
            doc.add(new TextField("name", StringUtils.defaultString(e.getName()), Field.Store.NO));
            doc.add(new StoredField("foundling_title", StringUtils.defaultString(e.getUsername())));
            indexManager.addOrUpdate(docId(Foundling.FoundlingType.USER, e.getId().toString()), doc);
        }
    }

    private void indexUploads() throws IOException {
        List<UploadEntity> entities = entityManager
                .createQuery("SELECT u FROM UploadEntity u", UploadEntity.class)
                .getResultList();
        for (UploadEntity e : entities) {
            Document doc = new Document();
            doc.add(new StringField("doc_id", docId(Foundling.FoundlingType.UPLOAD, e.getId().toString()), Field.Store.NO));
            doc.add(new StringField("type", Foundling.FoundlingType.UPLOAD.name(), Field.Store.YES));
            doc.add(new StoredField("id", e.getId().toString()));
            doc.add(new TextField("filename", StringUtils.defaultString(e.getFilename()), Field.Store.NO));
            doc.add(new TextField("description", StringUtils.defaultString(e.getDescription()), Field.Store.NO));
            doc.add(new StoredField("foundling_title", StringUtils.defaultString(e.getFilename())));
            doc.add(new StoredField("foundling_content", StringUtils.defaultString(e.getDescription())));
            indexManager.addOrUpdate(docId(Foundling.FoundlingType.UPLOAD, e.getId().toString()), doc);
        }
    }

    private static String docId(Foundling.FoundlingType type, String id) {
        return type.name() + ":" + id;
    }
}
