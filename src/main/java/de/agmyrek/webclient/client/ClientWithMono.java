package de.agmyrek.webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientWithMono {

    private final WebClient webClient;
    private final ClientProperties clientProperties;

    JsonNode getMono() {
        return this.webClient
                .get()
                .uri(clientProperties.getUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(e -> log.error("Fehler bei der Anfrage.", e))
                .doOnSuccess(response -> log.info("Response Payload: {}", response))
                .doOnError(WebClientResponseException.class, this::logWebclientException)
                .block();
    }

    void postMono(JsonNode payload, String ressource) {

        this.webClient
                .post()
                .uri(clientProperties.getUri() + ressource)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payload)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .toBodilessEntity()
                .doOnError(e -> log.error("Fehler bei der Anfrage.", e))
                .doOnSuccess(response -> log.info("Response Payload: {}", response))
                .doOnError(WebClientResponseException.class, this::logWebclientException)
                .block();
    }

    Optional<JsonNode> getMonoWithOptional() {
        return this.webClient
                .get()
                .uri(clientProperties.getUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(throwable -> {
                    log.error("Fehler bei der Anfrage.", throwable);
                    return Mono.empty();
                })
                .doOnSuccess(response -> log.info("Response Payload: {}", response))
                .blockOptional();
    }

    JsonNode getMonoOnStatus() {
        return this.webClient
                .get()
                .uri(clientProperties.getUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::is3xxRedirection, clientResponse -> Mono.error(new IllegalArgumentException("Nicht authentifiziert")))
                .bodyToMono(JsonNode.class)
                .doOnError(e -> log.error("Fehler bei der Anfrage.", e))
                .doOnSuccess(response -> log.info("Response Payload: {}", response))
                .block();
    }

    private void logWebclientException(WebClientResponseException exception) {
        var responseBody = exception.getResponseBodyAsString();
        if (StringUtils.hasText(responseBody)) {
            log.error("Response Body: {}", responseBody);
        }
    }
}
