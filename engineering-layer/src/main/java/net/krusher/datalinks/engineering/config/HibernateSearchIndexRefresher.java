package net.krusher.datalinks.engineering.config;

import jakarta.persistence.EntityManager;
import lombok.NonNull;
import lombok.extern.log4j.Log4j2;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Log4j2
public class HibernateSearchIndexRefresher implements ApplicationListener<ContextRefreshedEvent> {

    private final EntityManager entityManager;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Autowired
    public HibernateSearchIndexRefresher(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
        if (activeProfile.equals("dev")) {
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