package net.krusher.datalinks.engineering.config;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.TextField;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class LuceneIndexManagerTest {

    @Test
    void canInitAddCommitAndClose() throws IOException {
        Path temp = Files.createTempDirectory("lucene-test");
        String path = temp.toAbsolutePath().toString();
        LuceneIndexManager manager = new LuceneIndexManager(path);
        try {
            manager.init();

            Document doc = new Document();
            doc.add(new StringField("doc_id", "1", Store.YES));
            doc.add(new TextField("content", "hello world", Store.NO));

            assertDoesNotThrow(() -> manager.addOrUpdate("1", doc));
            assertDoesNotThrow(manager::commit);

            // withSearcher should succeed
            assertDoesNotThrow(() -> manager.withSearcher(searcher -> searcher.getIndexReader().numDocs()));
        } finally {
            // ensure close
            manager.close();
            // cleanup temp dir
            Files.walk(temp)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }
}
