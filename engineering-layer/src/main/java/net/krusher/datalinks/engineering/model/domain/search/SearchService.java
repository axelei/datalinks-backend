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
import net.krusher.datalinks.engineering.model.domain.page.PageEntity;
import net.krusher.datalinks.engineering.model.domain.upload.UploadEntity;
import net.krusher.datalinks.engineering.model.domain.user.UserEntity;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
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

    public void indexPage(PageEntity e) {
        try {
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
            indexManager.commit();
        } catch (IOException ex) {
            log.error("Failed to index page " + e.getId(), ex);
        }
    }

    public void indexCategory(CategoryEntity e) {
        try {
            Document doc = new Document();
            doc.add(new StringField("doc_id", docId(Foundling.FoundlingType.CATEGORY, e.getId().toString()), Field.Store.NO));
            doc.add(new StringField("type", Foundling.FoundlingType.CATEGORY.name(), Field.Store.YES));
            doc.add(new StoredField("id", e.getId().toString()));
            doc.add(new TextField("name", StringUtils.defaultString(e.getName()), Field.Store.NO));
            doc.add(new StoredField("foundling_title", StringUtils.defaultString(e.getName())));
            indexManager.addOrUpdate(docId(Foundling.FoundlingType.CATEGORY, e.getId().toString()), doc);
            indexManager.commit();
        } catch (IOException ex) {
            log.error("Failed to index category " + e.getId(), ex);
        }
    }

    public void indexUser(UserEntity e) {
        try {
            Document doc = new Document();
            doc.add(new StringField("doc_id", docId(Foundling.FoundlingType.USER, e.getId().toString()), Field.Store.NO));
            doc.add(new StringField("type", Foundling.FoundlingType.USER.name(), Field.Store.YES));
            doc.add(new StoredField("id", e.getId().toString()));
            doc.add(new TextField("username", StringUtils.defaultString(e.getUsername()), Field.Store.NO));
            doc.add(new TextField("name", StringUtils.defaultString(e.getName()), Field.Store.NO));
            doc.add(new StoredField("foundling_title", StringUtils.defaultString(e.getUsername())));
            indexManager.addOrUpdate(docId(Foundling.FoundlingType.USER, e.getId().toString()), doc);
            indexManager.commit();
        } catch (IOException ex) {
            log.error("Failed to index user " + e.getId(), ex);
        }
    }

    public void indexUpload(UploadEntity e) {
        try {
            Document doc = new Document();
            doc.add(new StringField("doc_id", docId(Foundling.FoundlingType.UPLOAD, e.getId().toString()), Field.Store.NO));
            doc.add(new StringField("type", Foundling.FoundlingType.UPLOAD.name(), Field.Store.YES));
            doc.add(new StoredField("id", e.getId().toString()));
            doc.add(new TextField("filename", StringUtils.defaultString(e.getFilename()), Field.Store.NO));
            doc.add(new TextField("description", StringUtils.defaultString(e.getDescription()), Field.Store.NO));
            doc.add(new StoredField("foundling_title", StringUtils.defaultString(e.getFilename())));
            doc.add(new StoredField("foundling_content", StringUtils.defaultString(e.getDescription())));
            indexManager.addOrUpdate(docId(Foundling.FoundlingType.UPLOAD, e.getId().toString()), doc);
            indexManager.commit();
        } catch (IOException ex) {
            log.error("Failed to index upload " + e.getId(), ex);
        }
    }

    private static String docId(Foundling.FoundlingType type, String id) {
        return type.name() + ":" + id;
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
