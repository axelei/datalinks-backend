package net.krusher.datalinks.engineering.config;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import io.quarkus.runtime.StartupEvent;
import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.engineering.model.domain.search.SearchService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class LuceneIndexRefresherTest {

    @Test
    void onStartDeletesAndIndexes() throws IOException {
        EntityManager em = Mockito.mock(EntityManager.class);
        LuceneIndexManager manager = Mockito.mock(LuceneIndexManager.class);
        SearchService searchService = Mockito.mock(SearchService.class);

        TypedQuery<PageEntity> q = Mockito.mock(TypedQuery.class);
        when(em.createQuery(any(String.class), any(Class.class))).thenReturn((TypedQuery) q);
        when(q.getResultList()).thenReturn(List.of());

        LuceneIndexRefresher refresher = new LuceneIndexRefresher(em, manager, searchService);

        refresher.onStart(new StartupEvent());

        verify(manager).deleteAll();
        verify(manager).commit();
    }
}
