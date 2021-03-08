package de.agmyrek.webclient.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientWithFlux {

    private final WebClient webClient;
    private final ClientProperties clientProperties;


    List<JsonNode> getFlux() {
        return this.webClient
                .get()
                .uri(clientProperties.getUri())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(JsonNode.class)
                .doOnError(e -> log.error("Fehler bei der Anfrage.", e))
                .collectList()
                .block();
    }
}
