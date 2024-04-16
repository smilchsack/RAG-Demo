package de.swf.rag;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.StreamingResponseHandler;
import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final MilvusEmbeddingStore embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final StreamingChatLanguageModel chatModel;

    private Embedding embeddingOf(final String question) {
        return embeddingModel.embed(question).content();
    }

    private List<EmbeddingMatch<TextSegment>> findRelevantEmbeddings(final Embedding questionEmbedding,
                                                                     final int maxEmbeddings,
                                                                     final double relevanceValue) {
        return embeddingStore.findRelevant(questionEmbedding, maxEmbeddings, relevanceValue);
    }

    private Prompt applyFor(final String question,
                            final List<EmbeddingMatch<TextSegment>> relevantEmbeddings) {
        // Create a prompt for the model that includes question and relevant embeddings
        final PromptTemplate promptTemplate = PromptTemplate.from(
                """
                        Answer the question based on the context below and use the history of the conversation to continue
                        If the question cannot be answered using the information provided answer with "I don't know"
                        Keep your answers short and precise

                        Question:
                        {{question}}

                        Base your answer on the following information:
                        {{information}}""");

        final String information = relevantEmbeddings.stream()
                .map(match -> match.embedded().text())
                .collect(joining("\n\n"));


        final Map<String, Object> variables = new HashMap<>();
        variables.put("question", question);
        variables.put("information", information);

        return promptTemplate.apply(variables);
    }

    public Flux<ServerSentEvent<Token>> ask(final String question,
                                            final int maxEmbeddings,
                                            final double relevanceValue) {

        // Frage in einen Vektor umwandeln.
        final Embedding questionEmbedding = embeddingOf(question);

        // Die Vektordatenbank nach relevanten Informationen durchsuchen.
        final List<EmbeddingMatch<TextSegment>> relevantEmbeddings
                = findRelevantEmbeddings(questionEmbedding, maxEmbeddings, relevanceValue);

        // Die Abfrage an das LLM vorbereiten.
        final Prompt prompt = applyFor(question, relevantEmbeddings);

        // Die Abfrage an das LLM durchführen. Die Ergebnisse werden per Streaming zurückgegeben.
        return Flux.create(sink -> chatModel.generate(prompt.toUserMessage(), new StreamingResponseHandler<>() {
            @Override
            public void onNext(String s) {
                sink.next(ServerSentEvent.builder(new Token(s)).build());
                log.debug("next token: " + s);
            }

            @Override
            public void onComplete(Response<AiMessage> response) {
                log.debug("Question is answered!");
                sink.next(ServerSentEvent.builder(new Token(""))
                        .event("COMPLETE").build());
                StreamingResponseHandler.super.onComplete(response);
                sink.complete();
            }

            @Override
            public void onError(Throwable throwable) {
                log.error(throwable.getMessage());
                sink.error(throwable);
            }
        }));
    }
}
