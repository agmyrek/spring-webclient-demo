package de.agmyrek.webclient.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
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
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}
