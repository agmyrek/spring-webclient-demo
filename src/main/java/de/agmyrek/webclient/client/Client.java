package de.agmyrek.webclient.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class Client {

    @Autowired
    private final WebClient webClient;
    @Autowired
    private final ClientProperties clientProperties;

    public Client(WebClient webClient, ClientProperties clientProperties){
        this.webClient = webClient;
        this.clientProperties = clientProperties;
    }

    Mono<ResponseDto> get(RequestDto requestDto) {
        return this.webClient
                .get()
                .uri(clientProperties.getUrl())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ResponseDto.class)
                .doOnError(e -> log.error("Fehler bei der Anfrage.", e))
                .doOnSuccess(response -> log.info("Response Payload: {}", response));
    }
}
