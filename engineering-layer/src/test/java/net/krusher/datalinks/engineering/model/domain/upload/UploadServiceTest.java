package net.krusher.datalinks.engineering.model.domain.upload;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.domain.model.upload.Upload;
import net.krusher.datalinks.engineering.mapper.PageMapper;
import net.krusher.datalinks.engineering.mapper.UploadMapper;
import net.krusher.datalinks.engineering.model.domain.page.PageRepositoryBean;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UploadServiceTest {

    @Test
    void updateCallsMergeAndIndex() {
        UploadMapper mapper = Mockito.mock(UploadMapper.class);
        UploadRepositoryBean repo = Mockito.mock(UploadRepositoryBean.class);
        UploadUsageRepositoryBean usageRepo = Mockito.mock(UploadUsageRepositoryBean.class);
        PageRepositoryBean pageRepo = Mockito.mock(PageRepositoryBean.class);
        EntityManager em = Mockito.mock(EntityManager.class);
        PageMapper pageMapper = Mockito.mock(PageMapper.class);
        SearchService searchService = Mockito.mock(SearchService.class);

        UploadService svc = new UploadService(mapper, repo, usageRepo, pageRepo, em, pageMapper, searchService);

        Upload upload = Upload.builder().slug("s").build();
        when(mapper.toEntity(upload)).thenReturn(new UploadEntity());
        when(em.merge(any())).thenReturn(new UploadEntity());

        svc.update(upload);
        verify(em).merge(any());
        verify(searchService).indexUpload(any());
    }

    @Test
    void saveUsageCallsMerge() {
        UploadMapper mapper = Mockito.mock(UploadMapper.class);
        UploadRepositoryBean repo = Mockito.mock(UploadRepositoryBean.class);
        UploadUsageRepositoryBean usageRepo = Mockito.mock(UploadUsageRepositoryBean.class);
        PageRepositoryBean pageRepo = Mockito.mock(PageRepositoryBean.class);
        EntityManager em = Mockito.mock(EntityManager.class);
        PageMapper pageMapper = Mockito.mock(PageMapper.class);
        SearchService searchService = Mockito.mock(SearchService.class);

        UploadService svc = new UploadService(mapper, repo, usageRepo, pageRepo, em, pageMapper, searchService);

        UploadUsageEntity usage = new UploadUsageEntity();
        svc.saveUsage(usage);
        verify(em).merge(usage);
    }
}
