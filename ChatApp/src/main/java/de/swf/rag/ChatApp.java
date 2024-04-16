package de.swf.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.UrlDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.parser.apache.pdfbox.ApachePdfBoxDocumentParser;
import dev.langchain4j.data.document.transformer.HtmlTextExtractor;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.print.Doc;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import static dev.langchain4j.data.document.loader.FileSystemDocumentLoader.loadDocument;

@Slf4j
@SpringBootApplication
public class ChatApp {

    private final EmbeddingStoreIngestor embeddingStoreIngestor;

    private final MilvusEmbeddingStore embeddingStore;

    private final EmbeddingModel embeddingModel;

    public ChatApp(final EmbeddingStoreIngestor embeddingStoreIngestor,
                   final MilvusEmbeddingStore embeddingStore,
                   final EmbeddingModel embeddingModel) {
        this.embeddingStoreIngestor = embeddingStoreIngestor;
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @PostConstruct
    public void init() {
        // @TODO: Ein Hack, um herauszufinden, ob in der VektorDB schon etwas drin ist.
        final Embedding queryEmbedding = embeddingModel.embed("milvus").content();
        final List<EmbeddingMatch<TextSegment>> relevant = embeddingStore.findRelevant(queryEmbedding, 1);

        if (relevant != null && relevant.isEmpty()) {
            log.info("Loading documents...");
            ingestPDF("data/HNSW_(1603.09320).pdf");
            ingestPDF("data/Milvus_(3448016.3457550).pdf");

            ingestHtml("https://www.pinecone.io/learn/series/faiss/hnsw/");
            ingestHtml("https://zilliz.com/learn/hierarchical-navigable-small-worlds-HNSW");
        }
    }

    private Document loadDocumentFrom(final String url) {
        try {
            final Document htmlDocument = UrlDocumentLoader.load(new URL(url), new TextDocumentParser());
            final HtmlTextExtractor transformer = new HtmlTextExtractor();
            return transformer.transform(htmlDocument);
        } catch (MalformedURLException e) {
            log.error("Error loading Webpage!", e);
        }
        return null;
    }

    private void ingestHtml(final String url) {
        final Document document = loadDocumentFrom(url);
        ingest(document);
    }

    private void ingest(final Document document) {
        embeddingStoreIngestor.ingest(document);
    }

    private void ingestPDF(final String path) {
        final Document document = loadDocument(toPath(path), new ApachePdfBoxDocumentParser());
        ingest(document);
    }

    private static Path toPath(String fileName) {
        try {
            URL fileUrl = ChatApp.class.getClassLoader().getResource(fileName);
            return Paths.get(Objects.requireNonNull(fileUrl).toURI());
        } catch (URISyntaxException e) {
            log.error("Error loading " + fileName);
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(ChatApp.class, args);
    }

}
