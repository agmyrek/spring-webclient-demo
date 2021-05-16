package de.agmyrek.webclient.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

class ClientWithFluxTest {

    static ClientWithFlux client;
    static MockWebServer mockWebServer;

    @BeforeAll
    static void setup(){
        mockWebServer = new MockWebServer();
        var mockedUri = "http://localhost:" + mockWebServer.getPort();
        var clientProperties = new ClientProperties(mockedUri);
        var webclient = WebClient.builder().build();
        client = new ClientWithFlux(webclient, clientProperties);
    }

    @Test
    void getFlux() {
        var responseBody = """
                [
                    {
                        "name": "Max"
                    },
                    {
                        "name": "Moritz"
                    }
                ]
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(responseBody)
        );
        var responseDto = client.getFlux();

        Assertions.assertThat(responseDto.size()).isEqualTo(2);
        Assertions.assertThat(responseDto.get(0).get("name").asText()).isEqualTo("Max");
        Assertions.assertThat(responseDto.get(1).get("name").asText()).isEqualTo("Moritz");
    }

    @Test
    void getFluxLeereAntwort() {
        var responseBody = """
                [
                ]
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(responseBody)
        );
        var responseDto = client.getFlux();

        Assertions.assertThat(responseDto.isEmpty()).isTrue();
    }
}