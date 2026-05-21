package net.krusher.datalinks.engineering.config;

import org.apache.lucene.analysis.Analyzer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class LuceneAnalyzersTest {

    @Test
    void edgeNGramAnalyzerIsBuilt() {
        Analyzer a = LuceneAnalyzers.edgeNGramAnalyzer();
        assertNotNull(a);
    }
}
