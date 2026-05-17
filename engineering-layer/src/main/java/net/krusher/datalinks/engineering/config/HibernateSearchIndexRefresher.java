package net.krusher.datalinks.engineering.config;

import io.quarkus.runtime.LaunchMode;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.jbosslog.JBossLog;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;

@ApplicationScoped
@JBossLog
public class HibernateSearchIndexRefresher {

    private final EntityManager entityManager;

    @Inject
    public HibernateSearchIndexRefresher(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        if (LaunchMode.current() == LaunchMode.DEVELOPMENT) {
            try {
                SearchSession searchSession = Search.session(entityManager);
                searchSession.massIndexer()
                        .threadsToLoadObjects(4)
                        .startAndWait();
                log.info("Hibernate Search indexes refreshed");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Error generating Hibernate Search indexes", e);
            }
        }
    }
}
