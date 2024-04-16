package de.swf.rag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatService service;

    @ResponseBody
    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<Token>> chatWithPdf(@RequestParam(defaultValue = "") final  String question,
                                                    @RequestParam(defaultValue = "3") @Min(1) @Max(7) final int maxEmbeddings,
                                                    @RequestParam(defaultValue = "0.7") @Min(0) @Max(1) final double relevanceValue) {
        log.info("Question: " + question + " MaxEmbeddings: " + maxEmbeddings + " RelevanceValue: " + relevanceValue);
        return service.ask(question, maxEmbeddings, relevanceValue);
    }
}
