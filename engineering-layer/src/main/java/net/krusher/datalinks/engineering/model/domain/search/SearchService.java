package net.krusher.datalinks.engineering.model.domain.search;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import lombok.extern.jbosslog.JBossLog;
import net.krusher.datalinks.engineering.config.LuceneIndexManager;
import net.krusher.datalinks.engineering.mapper.CategoryMapper;
import net.krusher.datalinks.engineering.model.domain.page.CategoryEntity;
import net.krusher.datalinks.domain.model.page.Category;
import net.krusher.datalinks.domain.model.search.Foundling;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
@JBossLog
public class SearchService {

    private static final int QUICK_LIMIT = 10;
    private static final String[] TITLE_FIELDS = {"title", "filename", "username", "name"};
    private static final String[] FULL_FIELDS = {"title", "content", "filename", "description", "name", "username"};

    private final EntityManager entityManager;
    private final CategoryMapper categoryMapper;
    private final LuceneIndexManager indexManager;

    @Inject
    public SearchService(EntityManager entityManager, CategoryMapper categoryMapper, LuceneIndexManager indexManager) {
        this.entityManager = entityManager;
        this.categoryMapper = categoryMapper;
        this.indexManager = indexManager;
    }

    public List<Foundling> titleSearch(String query) {
        Query luceneQuery = buildMultiFieldQuery(query, TITLE_FIELDS);
        if (luceneQuery == null) {
            return List.of();
        }
        return runQuery(luceneQuery, 0, QUICK_LIMIT).stream()
                .map(this::toFoundling)
                .toList();
    }

    public List<Foundling> search(String query, int page, int pageSize) {
        Query luceneQuery = buildMultiFieldQuery(query, FULL_FIELDS);
        if (luceneQuery == null) {
            return List.of();
        }
        return runQuery(luceneQuery, page * pageSize, pageSize).stream()
                .map(this::toFoundling)
                .toList();
    }

    public List<Category> searchCategories(String query) {
        Query baseQuery = buildMultiFieldQuery(query, new String[]{"name"});
        if (baseQuery == null) {
            return List.of();
        }
        Query typeFilter = new TermQuery(new Term("type", Foundling.FoundlingType.CATEGORY.name()));
        Query luceneQuery = new BooleanQuery.Builder()
                .add(baseQuery, BooleanClause.Occur.MUST)
                .add(typeFilter, BooleanClause.Occur.FILTER)
                .build();

        List<Category> categories = new ArrayList<>();
        for (Document doc : runQuery(luceneQuery, 0, QUICK_LIMIT)) {
            String idStr = doc.get("id");
            if (idStr == null) {
                continue;
            }
            CategoryEntity entity = entityManager.find(CategoryEntity.class, UUID.fromString(idStr));
            if (entity != null) {
                categories.add(categoryMapper.toModel(entity));
            }
        }
        return categories;
    }

    private Query buildMultiFieldQuery(String query, String[] fields) {
        if (query == null || query.isBlank()) {
            return null;
        }
        Analyzer analyzer = indexManager.getAnalyzer();
        BooleanQuery.Builder root = new BooleanQuery.Builder();
        boolean hasClauses = false;
        for (String field : fields) {
            List<String> tokens = analyze(analyzer, field, query);
            if (tokens.isEmpty()) {
                continue;
            }
            BooleanQuery.Builder fieldBuilder = new BooleanQuery.Builder();
            for (String token : tokens) {
                fieldBuilder.add(new TermQuery(new Term(field, token)), BooleanClause.Occur.SHOULD);
            }
            root.add(fieldBuilder.build(), BooleanClause.Occur.SHOULD);
            hasClauses = true;
        }
        return hasClauses ? root.build() : null;
    }

    private List<String> analyze(Analyzer analyzer, String field, String text) {
        List<String> tokens = new ArrayList<>();
        try (TokenStream ts = analyzer.tokenStream(field, text)) {
            CharTermAttribute term = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                tokens.add(term.toString());
            }
            ts.end();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return tokens;
    }

    private List<Document> runQuery(Query query, int offset, int limit) {
        try {
            return indexManager.withSearcher(searcher -> {
                TopDocs topDocs = searcher.search(query, offset + limit);
                ScoreDoc[] hits = topDocs.scoreDocs;
                List<Document> docs = new ArrayList<>();
                for (int i = offset; i < hits.length && docs.size() < limit; i++) {
                    docs.add(searcher.doc(hits[i].doc));
                }
                return docs;
            });
        } catch (IOException e) {
            log.error("Lucene search failed", e);
            return List.of();
        }
    }

    private Foundling toFoundling(Document doc) {
        return Foundling.builder()
                .id(UUID.fromString(doc.get("id")))
                .title(doc.get("foundling_title"))
                .content(doc.get("foundling_content"))
                .type(Foundling.FoundlingType.valueOf(doc.get("type")))
                .build();
    }
}
