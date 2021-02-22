package de.agmyrek.webclient.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@Slf4j
public class WebClientAutoConfiguration {

    private final static int TIMEOUT_IN_SECONDS = 5;

    @Bean
    public WebClient createWebClient(WebClient.Builder webClientBuilder) {
        final HttpClient httpClient = HttpClient.create()
                .wiretap(true) // nur fürs Debugging anschalten
                .responseTimeout(Duration.ofSeconds(TIMEOUT_IN_SECONDS));

        //Damit der erste Request nicht länger dauert:
        httpClient.warmup().block();

        return webClientBuilder
                .filter(logRequest())
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    // loggt jeden Request des Clients
    private static ExchangeFilterFunction logRequest() {
        return ExchangeFilterFunction.ofRequestProcessor(request -> {
            log.info("Request: {} {}", request.method(), request.url());
            return Mono.just(request);
        });
    }
}
