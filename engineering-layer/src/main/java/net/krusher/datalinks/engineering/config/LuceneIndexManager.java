package net.krusher.datalinks.engineering.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.jbosslog.JBossLog;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.SearcherFactory;
import org.apache.lucene.search.SearcherManager;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@ApplicationScoped
@JBossLog
public class LuceneIndexManager {

    private final String indexDir;
    private Directory directory;
    private Analyzer analyzer;
    private IndexWriter writer;
    private SearcherManager searcherManager;

    public LuceneIndexManager(
            @ConfigProperty(name = "application.search.index-dir", defaultValue = "./data/lucene-index") String indexDir) {
        this.indexDir = indexDir;
    }

    @PostConstruct
    void init() {
        try {
            Path path = Paths.get(indexDir);
            Files.createDirectories(path);
            directory = FSDirectory.open(path);
            analyzer = LuceneAnalyzers.edgeNGramAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            writer = new IndexWriter(directory, config);
            writer.commit();
            searcherManager = new SearcherManager(writer, true, true, new SearcherFactory());
            log.infof("Lucene index initialized at %s", path.toAbsolutePath());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize Lucene index at " + indexDir, e);
        }
    }

    @PreDestroy
    void close() {
        closeQuietly("SearcherManager", searcherManager);
        closeQuietly("IndexWriter", writer);
        closeQuietly("Directory", directory);
        closeQuietly("Analyzer", analyzer);
    }

    private void closeQuietly(String name, AutoCloseable closeable) {
        if (closeable == null) {
            return;
        }
        try {
            closeable.close();
        } catch (Exception e) {
            log.errorf(e, "Error closing %s", name);
        }
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public synchronized void addOrUpdate(String docId, Document doc) throws IOException {
        writer.updateDocument(new Term("doc_id", docId), doc);
    }

    public synchronized void deleteAll() throws IOException {
        writer.deleteAll();
    }

    public synchronized void commit() throws IOException {
        writer.commit();
        searcherManager.maybeRefresh();
    }

    public <T> T withSearcher(SearcherFunction<T> fn) throws IOException {
        searcherManager.maybeRefresh();
        IndexSearcher searcher = searcherManager.acquire();
        try {
            return fn.apply(searcher);
        } finally {
            searcherManager.release(searcher);
        }
    }

    @FunctionalInterface
    public interface SearcherFunction<T> {
        T apply(IndexSearcher searcher) throws IOException;
    }
}
