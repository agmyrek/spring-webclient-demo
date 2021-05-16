package de.agmyrek.webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientWithMonoPost {

    private final WebClient webClient;
    private final ClientProperties clientProperties;


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

    private void logWebclientException(WebClientResponseException exception) {
        var responseBody = exception.getResponseBodyAsString();
        if (StringUtils.hasText(responseBody)) {
            log.error("Response Body: {}", responseBody);
        }
    }
}
