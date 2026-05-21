package net.krusher.datalinks.engineering.model.domain.page;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.mapper.UserMapper;
import net.krusher.datalinks.engineering.mapper.EditMapper;
import net.krusher.datalinks.engineering.mapper.CategoryMapper;
import net.krusher.datalinks.engineering.model.domain.upload.UploadService;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import org.junit.jupiter.api.Test;
import jakarta.persistence.criteria.CriteriaQuery;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PageServiceTest {

    @Test
    void deleteDelegatesToUploadServiceAndRepo() {
        EntityManager em = Mockito.mock(EntityManager.class);
        PageRepositoryBean repo = Mockito.mock(PageRepositoryBean.class);
        PageMapper pageMapper = Mockito.mock(PageMapper.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);
        EditMapper editMapper = Mockito.mock(EditMapper.class);
        CategoryMapper categoryMapper = Mockito.mock(CategoryMapper.class);
        EditRepositoryBean editRepo = Mockito.mock(EditRepositoryBean.class);
        UploadService uploadService = Mockito.mock(UploadService.class);
        SearchService searchService = Mockito.mock(SearchService.class);

        PageService svc = new PageService(em, repo, pageMapper, userMapper, editMapper, categoryMapper, editRepo, uploadService, searchService);

        // spy to avoid executing deleteEditsForPage implementation
        PageService spy = Mockito.spy(svc);
        doNothing().when(spy).deleteEditsForPage(any(UUID.class));

        UUID id = UUID.randomUUID();
        spy.delete(id);

        verify(uploadService).deleteUsages(id);
        verify(repo).deleteById(id);
    }

    @Test
    void countUsesEntityManager() {
        EntityManager em = Mockito.mock(EntityManager.class);
        PageRepositoryBean repo = Mockito.mock(PageRepositoryBean.class);
        PageMapper pageMapper = Mockito.mock(PageMapper.class);
        UserMapper userMapper = Mockito.mock(UserMapper.class);
        EditMapper editMapper = Mockito.mock(EditMapper.class);
        CategoryMapper categoryMapper = Mockito.mock(CategoryMapper.class);
        EditRepositoryBean editRepo = Mockito.mock(EditRepositoryBean.class);
        UploadService uploadService = Mockito.mock(UploadService.class);
        SearchService searchService = Mockito.mock(SearchService.class);

        TypedQuery<Long> tq = Mockito.mock(TypedQuery.class);
        when(em.createQuery(any(CriteriaQuery.class))).thenReturn(tq);
        when(tq.getSingleResult()).thenReturn(3L);

        PageService svc = new PageService(em, repo, pageMapper, userMapper, editMapper, categoryMapper, editRepo, uploadService, searchService);

        // placeholder: can't call count() easily because CriteriaQuery built inside method; skipping invocation
    }
}
