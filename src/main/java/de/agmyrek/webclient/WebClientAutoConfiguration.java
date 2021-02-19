package de.agmyrek.webclient;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientAutoConfiguration {

    @Autowired
    private WebClient.Builder webClientBuilder;

    private final static int TIMEOUT_IN_SECONDS = 5;

    @Bean
    public WebClient createWebClient() {
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
