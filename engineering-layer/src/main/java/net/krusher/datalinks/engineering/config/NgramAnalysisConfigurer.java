package net.krusher.datalinks.engineering.config;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Named;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurationContext;
import org.hibernate.search.backend.elasticsearch.analysis.ElasticsearchAnalysisConfigurer;

@Dependent
@Named("ngramAnalysisConfigurer")
public class NgramAnalysisConfigurer implements ElasticsearchAnalysisConfigurer {

    @Override
    public void configure(ElasticsearchAnalysisConfigurationContext context) {
        context.analyzer("edgeNGramAnalyzer").custom()
                .tokenizer("whitespace")
                .tokenFilters("lowercase", "stop", "limit_token_count_3", "edge_ngram_3_15");

        context.tokenFilter("limit_token_count_3")
                .type("limit")
                .param("max_token_count", 3);

        context.tokenFilter("edge_ngram_3_15")
                .type("edge_ngram")
                .param("min_gram", 3)
                .param("max_gram", 15);
    }
}
