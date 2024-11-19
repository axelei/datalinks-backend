package net.krusher.datalinks.engineering.model.domain.search;

import jakarta.persistence.EntityManager;
import net.krusher.datalinks.engineering.mapper.CategoryMapper;
import net.krusher.datalinks.engineering.model.domain.page.CategoryEntity;
import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.engineering.model.domain.upload.UploadEntity;
import net.krusher.datalinks.model.page.Category;
import net.krusher.datalinks.model.search.Foundable;
import net.krusher.datalinks.model.search.Foundling;
import org.hibernate.search.engine.search.query.SearchQuery;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final EntityManager entityManager;
    private final CategoryMapper categoryMapper;

    @Autowired
    public SearchService(EntityManager entityManager, CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
        this.entityManager = entityManager;
    }

    public List<Foundling> titleSearch(String query) {
        SearchSession searchSession = Search.session(entityManager);
        SearchQuery<Foundable> search = searchSession.search(List.of(PageEntity.class, UploadEntity.class))
                .where(f -> f.match()
                        .fields("title", "filename")
                        .matching(query)
                        .fuzzy()
                ).toQuery();
        SearchResult<Foundable> pages = search.fetch(10);
        return pages.hits().stream().map(Foundable::toFoundling).toList();
    }

    public List<Foundling> search(String query, int page, int pageSize) {
        SearchSession searchSession = Search.session(entityManager);
        SearchQuery<Foundable> search = searchSession.search(List.of(PageEntity.class, UploadEntity.class))
                .where(f -> f.match()
                        .fields("title", "content", "filename", "description")
                        .matching(query)
                        .fuzzy()
                ).toQuery();
        SearchResult<Foundable> pages = search.fetch(page * pageSize, pageSize);
        return pages.hits().stream().map(Foundable::toFoundling).toList();
    }

    public List<Category> searchCategories(String query) {
        SearchSession searchSession = Search.session(entityManager);
        SearchQuery<CategoryEntity> search = searchSession.search(List.of(CategoryEntity.class))
                .where(f -> f.match()
                        .fields("name")
                        .matching(query)
                        .fuzzy()
                ).toQuery();
        SearchResult<CategoryEntity> pages = search.fetch(10);
        return pages.hits().stream().map(categoryMapper::toModel).toList();
    }

}