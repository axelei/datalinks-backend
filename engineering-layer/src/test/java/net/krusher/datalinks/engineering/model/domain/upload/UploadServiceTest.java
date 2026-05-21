package net.krusher.datalinks.engineering.model.domain.upload;

import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import net.krusher.datalinks.domain.exception.EngineException;
import net.krusher.datalinks.domain.model.page.PageShort;
import net.krusher.datalinks.domain.model.upload.Upload;
import net.krusher.datalinks.engineering.mapper.UploadMapper;
import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@QuarkusTest
@TestTransaction
class UploadServiceTest {

    @Inject
    UploadService uploadService;

    @Inject
    EntityManager entityManager;

    @Inject
    UploadMapper uploadMapper;

    @InjectMock
    SearchService searchService;

    private static final String TEST_UPLOAD_DIR = "target/test-uploads";
    private static final Path UPLOAD_PATH = Path.of(TEST_UPLOAD_DIR);

    @BeforeAll
    static void setUpAll() throws IOException {
        Files.createDirectories(UPLOAD_PATH);
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        if (Files.exists(UPLOAD_PATH)) {
            Files.walk(UPLOAD_PATH)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException e) {
                            // ignore
                        }
                    });
        }
    }

    @Test
    void saveAndFindBySlug() throws IOException {
        String slug = "save-test-" + UUID.randomUUID();
        Upload upload = Upload.builder()
                .slug(slug)
                .filename("hello.txt")
                .description("Test description")
                .inputStream(new ByteArrayInputStream("Hello, World!".getBytes()))
                .build();

        uploadService.save(upload);

        Optional<Upload> found = uploadService.findBySlug(slug);
        assertTrue(found.isPresent());
        assertEquals(slug, found.get().getSlug());
        assertEquals("hello.txt", found.get().getFilename());
        assertEquals("Test description", found.get().getDescription());
        assertNotNull(found.get().getId());
        assertNotNull(found.get().getMd5());
        assertNotNull(found.get().getCreationDate());
        assertNotNull(found.get().getInputStream());

        verify(searchService).indexUpload(any(UploadEntity.class));
    }

    @Test
    void findBySlugReturnsEmptyForNonexistent() {
        Optional<Upload> found = uploadService.findBySlug("nonexistent-slug");
        assertTrue(found.isEmpty());
    }

    @Test
    void saveThrowsOnDuplicateSlug() throws IOException {
        String slug = "duplicate-test-" + UUID.randomUUID();
        Upload upload = Upload.builder()
                .slug(slug)
                .filename("original.txt")
                .inputStream(new ByteArrayInputStream("Original".getBytes()))
                .build();

        uploadService.save(upload);

        Upload duplicate = Upload.builder()
                .slug(slug)
                .filename("duplicate.txt")
                .inputStream(new ByteArrayInputStream("Duplicate".getBytes()))
                .build();

        assertThrows(EngineException.class, () -> uploadService.save(duplicate));
    }

    @Test
    void updateModifiesAndReindexes() {
        UploadEntity entity = UploadEntity.builder()
                .id(UUID.randomUUID())
                .slug("update-test-" + UUID.randomUUID())
                .filename("before.txt")
                .md5("d41d8cd98f00b204e9800998ecf8427e")
                .build();
        entityManager.persist(entity);
        entityManager.flush();

        Upload upload = uploadMapper.toModel(entity);
        upload.setFilename("after.txt");

        uploadService.update(upload);

        UploadEntity updated = entityManager.find(UploadEntity.class, entity.getId());
        assertEquals("after.txt", updated.getFilename());

        verify(searchService).indexUpload(any(UploadEntity.class));
    }

    @Test
    void saveUsage() {
        UploadUsageEntity usage = UploadUsageEntity.builder()
                .uploadId(UUID.randomUUID())
                .pageId(UUID.randomUUID())
                .build();

        uploadService.saveUsage(usage);

        List<UploadUsageEntity> found = entityManager
                .createQuery("FROM UploadUsageEntity WHERE uploadId = :uploadId AND pageId = :pageId", UploadUsageEntity.class)
                .setParameter("uploadId", usage.getUploadId())
                .setParameter("pageId", usage.getPageId())
                .getResultList();
        assertEquals(1, found.size());
    }

    @Test
    void findUsagesReturnsRelatedPages() {
        PageEntity page = PageEntity.builder()
                .slug("usage-page-" + UUID.randomUUID())
                .title("Usage Page Title")
                .content("Content")
                .build();
        entityManager.persist(page);
        entityManager.flush();

        UploadEntity upload = UploadEntity.builder()
                .id(UUID.randomUUID())
                .slug("usage-upload-" + UUID.randomUUID())
                .filename("test.txt")
                .md5("d41d8cd98f00b204e9800998ecf8427e")
                .build();
        entityManager.persist(upload);
        entityManager.flush();

        UploadUsageEntity usage = UploadUsageEntity.builder()
                .uploadId(upload.getId())
                .pageId(page.getId())
                .build();
        entityManager.persist(usage);
        entityManager.flush();

        List<PageShort> usages = uploadService.findUsages(upload.getId());
        assertEquals(1, usages.size());
        assertEquals(page.getTitle(), usages.get(0).getTitle());
        assertEquals(page.getSlug(), usages.get(0).getSlug());
    }

    @Test
    void deleteUsagesRemovesByPageId() {
        UUID pageId = UUID.randomUUID();

        UploadUsageEntity usage1 = UploadUsageEntity.builder()
                .uploadId(UUID.randomUUID())
                .pageId(pageId)
                .build();
        UploadUsageEntity usage2 = UploadUsageEntity.builder()
                .uploadId(UUID.randomUUID())
                .pageId(pageId)
                .build();
        entityManager.persist(usage1);
        entityManager.persist(usage2);
        entityManager.flush();

        uploadService.deleteUsages(pageId);

        List<UploadUsageEntity> remaining = entityManager
                .createQuery("FROM UploadUsageEntity WHERE pageId = :pageId", UploadUsageEntity.class)
                .setParameter("pageId", pageId)
                .getResultList();
        assertTrue(remaining.isEmpty());
    }

    @Test
    void deleteRemovesUploadFromDatabase() throws IOException {
        String slug = "delete-test-" + UUID.randomUUID();
        Upload upload = Upload.builder()
                .slug(slug)
                .filename("delete.txt")
                .inputStream(new ByteArrayInputStream("Delete me".getBytes()))
                .build();

        uploadService.save(upload);

        Upload saved = uploadService.findBySlug(slug).orElseThrow();
        UUID uploadId = saved.getId();
        uploadService.delete(saved);

        assertTrue(uploadService.findBySlug(slug).isEmpty());
        assertNull(entityManager.find(UploadEntity.class, uploadId));
    }

    @Test
    void newUploadsReturnsOrderedByCreationDateDesc() throws IOException {
        uploadService.save(Upload.builder()
                .slug("nu-1-" + UUID.randomUUID())
                .filename("first.txt")
                .inputStream(new ByteArrayInputStream("First".getBytes()))
                .build());

        uploadService.save(Upload.builder()
                .slug("nu-2-" + UUID.randomUUID())
                .filename("second.txt")
                .inputStream(new ByteArrayInputStream("Second".getBytes()))
                .build());

        List<Upload> result = uploadService.newUploads(0, 10);
        assertTrue(result.size() >= 2);

        for (int i = 0; i < result.size() - 1; i++) {
            assertTrue(result.get(i).getCreationDate().compareTo(result.get(i + 1).getCreationDate()) >= 0);
        }
    }

    @Test
    void newUploadsSupportsPagination() throws IOException {
        uploadService.save(Upload.builder()
                .slug("np-1-" + UUID.randomUUID())
                .filename("first.txt")
                .inputStream(new ByteArrayInputStream("1".getBytes()))
                .build());

        uploadService.save(Upload.builder()
                .slug("np-2-" + UUID.randomUUID())
                .filename("second.txt")
                .inputStream(new ByteArrayInputStream("2".getBytes()))
                .build());

        uploadService.save(Upload.builder()
                .slug("np-3-" + UUID.randomUUID())
                .filename("third.txt")
                .inputStream(new ByteArrayInputStream("3".getBytes()))
                .build());

        List<Upload> page1 = uploadService.newUploads(0, 2);
        assertEquals(2, page1.size());

        List<Upload> page2 = uploadService.newUploads(1, 2);
        assertEquals(1, page2.size());
    }
}
