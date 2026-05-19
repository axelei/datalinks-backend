package net.krusher.datalinks.engineering.config;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.custom.CustomAnalyzer;

import java.io.IOException;

public final class LuceneAnalyzers {

    private LuceneAnalyzers() {
    }

    public static Analyzer edgeNGramAnalyzer() {
        try {
            return CustomAnalyzer.builder()
                    .withTokenizer("whitespace")
                    .addTokenFilter("lowercase")
                    .addTokenFilter("stop")
                    .addTokenFilter("edgeNGram", "minGramSize", "3", "maxGramSize", "15")
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to build Lucene edge n-gram analyzer", e);
        }
    }
}
