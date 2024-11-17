package net.krusher.datalinks.engineering.config;

import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.StopFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountFilterFactory;
import org.apache.lucene.analysis.ngram.EdgeNGramFilterFactory;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurationContext;
import org.hibernate.search.backend.lucene.analysis.LuceneAnalysisConfigurer;

public class NgramAnalysisConfigurer implements LuceneAnalysisConfigurer {
	@Override
	public void configure(LuceneAnalysisConfigurationContext context) {
		context.analyzer( "edgeNGramAnalyzer" ).custom()
				.tokenizer( WhitespaceTokenizerFactory.class )
				.tokenFilter( StopFilterFactory.class )
				.tokenFilter( LowerCaseFilterFactory.class )
				.tokenFilter( LimitTokenCountFilterFactory.class )
				.param( "maxTokenCount", "3" )
				.tokenFilter( EdgeNGramFilterFactory.class )
				.param( "minGramSize", "3" )
				.param( "maxGramSize", "15" );
	}
}