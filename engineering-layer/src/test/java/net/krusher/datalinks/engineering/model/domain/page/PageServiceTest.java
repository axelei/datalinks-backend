package net.krusher.datalinks.engineering.model.domain.page;

import io.quarkus.cache.Cache;
import io.quarkus.cache.CacheName;
import io.quarkus.test.InjectMock;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import net.krusher.datalinks.domain.model.page.Edit;
import net.krusher.datalinks.domain.model.page.Page;
import net.krusher.datalinks.domain.model.page.PageShort;
import net.krusher.datalinks.domain.model.user.User;
import net.krusher.datalinks.domain.model.user.UserLevel;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestTransaction
class PageServiceTest {

    @Inject
    PageService pageService;

    @Inject
    EntityManager entityManager;

    @InjectMock
    UploadService uploadService;

    @InjectMock
    SearchService searchService;

    @Inject
    @CacheName("pageCount")
    Cache pageCountCache;

    private User testUser;
    private UserEntity testUserEntity;

    @BeforeEach
    void setUp() {
        testUserEntity = UserEntity.builder()
                .username("testuser")
                .level(UserLevel.USER)
                .build();
        entityManager.persist(testUserEntity);
        entityManager.flush();

        testUser = User.builder()
                .id(testUserEntity.getId())
                .username(testUserEntity.getUsername())
                .level(testUserEntity.getLevel())
                .build();
    }

    @Test
    void saveAndFindBySlug() {
        Page page = Page.builder()
                .slug("my-test-page")
                .title("My Test Page")
                .content("<p>Hello world</p>")
                .creator(testUser)
                .categories(new HashSet<>())
                .build();

        pageService.save(page, testUser, "127.0.0.1");

        Optional<Page> found = pageService.findBySlug("my-test-page");
        assertTrue(found.isPresent());
        assertEquals("my-test-page", found.get().getSlug());
        assertEquals("My Test Page", found.get().getTitle());
        assertEquals("<p>Hello world</p>", found.get().getContent());
        assertEquals("testuser", found.get().getCreator().getUsername());
        assertNotNull(found.get().getSummary());
        assertNotNull(found.get().getCreationDate());
        assertNotNull(found.get().getModifiedDate());
    }

    @Test
    void findShortBySlug() {
        Page page = Page.builder()
                .slug("short-test")
                .title("Short Test")
                .content("Short content")
                .creator(testUser)
                .categories(new HashSet<>())
                .build();
        pageService.save(page, testUser, "127.0.0.1");

        Optional<PageShort> found = pageService.findShortBySlug("short-test");
        assertTrue(found.isPresent());
        assertEquals("short-test", found.get().getSlug());
        assertEquals("Short Test", found.get().getTitle());
        assertNull(found.get().getCreator());
    }

    @Test
    void updateOrCreateCreatesNew() {
        pageService.updateOrCreate(
                Page.builder()
                        .slug("new-page")
                        .title("New Page")
                        .content("New content")
                        .creator(testUser)
                        .categories(new HashSet<>())
                        .build(),
                testUser,
                "127.0.0.1");

        Optional<Page> found = pageService.findBySlug("new-page");
        assertTrue(found.isPresent());
        assertEquals("New Page", found.get().getTitle());
    }

    @Test
    void updateOrCreateUpdatesExisting() {
        pageService.updateOrCreate(
                Page.builder()
                        .slug("update-test")
                        .title("Original Title")
                        .content("Original content")
                        .creator(testUser)
                        .categories(new HashSet<>())
                        .build(),
                testUser,
                "127.0.0.1");

        pageService.updateOrCreate(
                Page.builder()
                        .slug("update-test")
                        .title("Updated Title")
                        .content("Updated content")
                        .creator(testUser)
                        .categories(new HashSet<>())
                        .build(),
                testUser,
                "127.0.0.1");

        Optional<Page> found = pageService.findBySlug("update-test");
        assertTrue(found.isPresent());
        assertEquals("Updated Title", found.get().getTitle());
        assertEquals("Updated content", found.get().getContent());
    }

    @Test
    void delete() {
        Page page = Page.builder()
                .slug("delete-test")
                .title("Delete Test")
                .content("To be deleted")
                .creator(testUser)
                .categories(new HashSet<>())
                .build();
        pageService.save(page, testUser, "127.0.0.1");

        UUID pageId = pageService.findBySlug("delete-test").get().getId();
        pageService.delete(pageId);

        assertTrue(pageService.findBySlug("delete-test").isEmpty());
    }

    @Test
    void count() {
        pageService.save(Page.builder().slug("p1").title("P1").content("C1").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");
        pageService.save(Page.builder().slug("p2").title("P2").content("C2").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");

        pageCountCache.invalidateAll().await().indefinitely();
        assertEquals(2, pageService.count());
    }

    @Test
    void block() {
        pageService.save(Page.builder()
                .slug("block-test")
                .title("Block Test")
                .content("Block me")
                .creator(testUser)
                .categories(new HashSet<>())
                .build(), testUser, "ip");

        pageService.block("block-test", UserLevel.ADMIN, UserLevel.LIBRARIAN);

        Optional<Page> found = pageService.findBySlug("block-test");
        assertTrue(found.isPresent());
        assertEquals(UserLevel.ADMIN, found.get().getReadBlock());
        assertEquals(UserLevel.LIBRARIAN, found.get().getEditBlock());
    }

    @Test
    void allPages() {
        pageService.save(Page.builder().slug("a1").title("Alpha").content("C1").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");
        pageService.save(Page.builder().slug("a2").title("Beta").content("C2").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");
        pageService.save(Page.builder().slug("a3").title("Gamma").content("C3").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");

        List<PageShort> page1 = pageService.allPages(0, 2);
        assertEquals(2, page1.size());

        List<PageShort> page2 = pageService.allPages(1, 2);
        assertEquals(1, page2.size());
    }

    @Test
    void findAllTitles() {
        pageService.save(Page.builder().slug("t1").title("TitleOne").content("C").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");
        pageService.save(Page.builder().slug("t2").title("TitleTwo").content("C").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");

        List<String> titles = pageService.findAllTitles();
        assertTrue(titles.contains("TitleOne"));
        assertTrue(titles.contains("TitleTwo"));
    }

    @Test
    void pagesSortBy() {
        pageService.save(Page.builder().slug("z-first").title("Zeta").content("C").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");
        pageService.save(Page.builder().slug("a-first").title("Alpha").content("C").creator(testUser).categories(new HashSet<>()).build(), testUser, "ip");

        List<PageShort> sorted = pageService.pagesSortBy("title", 0, 10);
        assertEquals(2, sorted.size());
        assertEquals("Zeta", sorted.get(0).getTitle());
        assertEquals("Alpha", sorted.get(1).getTitle());
    }

    @Test
    void editsAreCreatedOnSave() {
        pageService.save(
                Page.builder().slug("edit-test").title("Edit Test").content("Edit content").creator(testUser).categories(new HashSet<>()).build(),
                testUser, "192.168.1.1");

        Page saved = pageService.findBySlug("edit-test").get();
        List<Edit> edits = pageService.findByPage(saved, 0, 10);
        assertEquals(1, edits.size());
        assertEquals("Edit content", edits.get(0).getContent());
        assertEquals("192.168.1.1", edits.get(0).getIp());
        assertEquals("testuser", edits.get(0).getUser().getUsername());
    }

    @Test
    void findByUser() {
        pageService.save(
                Page.builder().slug("user-test").title("User Test").content("Content").creator(testUser).categories(new HashSet<>()).build(),
                testUser, "127.0.0.1");

        List<Edit> edits = pageService.findByUser(testUser, 0, 10);
        assertEquals(1, edits.size());
    }

    @Test
    void findEditById() {
        pageService.save(
                Page.builder().slug("edit-id-test").title("Edit ID").content("Find me").creator(testUser).categories(new HashSet<>()).build(),
                testUser, "127.0.0.1");

        Page saved = pageService.findBySlug("edit-id-test").get();
        List<Edit> edits = pageService.findByPage(saved, 0, 10);
        assertEquals(1, edits.size());

        Optional<Edit> found = pageService.findEditById(edits.get(0).getId());
        assertTrue(found.isPresent());
        assertEquals("Find me", found.get().getContent());
    }

    @Test
    void editsSortBy() {
        pageService.save(
                Page.builder().slug("esb-test").title("Edits Sort").content("Version 1").creator(testUser).categories(new HashSet<>()).build(),
                testUser, "127.0.0.1");

        List<Edit> edits = pageService.editsSortBy("date", 0, 10);
        assertEquals(1, edits.size());
        assertEquals("Version 1", edits.get(0).getContent());
    }
}
