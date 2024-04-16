package de.swf.rag;

import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.time.Duration;

@Configuration
public class ChatAppConfiguration  {

    @Value( "${milvus.collection-name}" )
    private String collectionName;

    @Value( "${milvus.url}" )
    private String milvusUrl;

    @Value( "${ollama.url}" )
    private String ollamaUrl;

    @DependsOn("store")
    @Bean
    public EmbeddingModel embeddingModel() {
        return new AllMiniLmL6V2EmbeddingModel();
    }

    @DependsOn({"store"})
    @Bean
    public EmbeddingStoreIngestor embeddingStoreIngestor() {
        return EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(300, 0))
                .embeddingModel(embeddingModel())
                .embeddingStore(milvusEmbeddingStore())
                .build();
    }

    @Bean("store")
    public MilvusEmbeddingStore milvusEmbeddingStore() {

        return MilvusEmbeddingStore.builder()
                .uri(milvusUrl)
                .collectionName(collectionName)
                .dimension(384)
                .build();
    }

    @Bean
    OllamaStreamingChatModel streamingChatLanguageModel() {
        return OllamaStreamingChatModel.builder().modelName("mistral")
                .temperature(0.)
                .timeout(Duration.ofDays(1))
                .baseUrl(ollamaUrl)
                .build();
    }

    @Bean
    CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.applyPermitDefaultValues();

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }

}
