package de.agmyrek.webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class Client {

    private final WebClient webClient;
    private final ClientProperties clientProperties;

    Mono<JsonNode> get() {
        return this.webClient
                .get()
                .uri(clientProperties.getUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(e -> log.error("Fehler bei der Anfrage.", e))
                .doOnSuccess(response -> log.info("Response Payload: {}", response));
    }
}
