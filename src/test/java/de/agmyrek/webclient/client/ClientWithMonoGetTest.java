package de.agmyrek.webclient.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


class ClientWithMonoGetTest {

    static ClientWithMonoGet clientWithMono;
    static MockWebServer mockWebServer;

    @BeforeAll
    static void setup(){
        mockWebServer = new MockWebServer();
        var mockedUri = "http://localhost:" + mockWebServer.getPort();
        var clientProperties = new ClientProperties(mockedUri);
        var webclient = WebClient.builder().build();
        clientWithMono = new ClientWithMonoGet(webclient, clientProperties);
    }

    @Test
    void getMonoHappyPath() {
        var responseBody = """
                {
                    "name": "Mustermann"
                }
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .setBody(responseBody)
        );
        var responseDto = clientWithMono.getMono();
        Assertions
                .assertThat(responseDto.get("name").asText())
                .isEqualTo("Mustermann");

    }

    @ParameterizedTest
    @ValueSource(ints = { 400, 500})
    void getMonoWirftExceptionBeiHttpStatus(int statusCode) {
        var responseBody = """
                Fehler...
                """;
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(statusCode)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN)
                .setBody(responseBody)
        );

        Assertions
                .assertThatThrownBy(() -> clientWithMono.getMono())
                .isInstanceOf(WebClientResponseException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = { 204, 400, 500})
    void getMonoMitLeeremOptionalBeiHttpStatus(int statusCode) {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(statusCode)
                        .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        );
        var optionalResult = clientWithMono.getMonoWithOptional();
        Assertions.assertThat(optionalResult.isEmpty()).isTrue();
    }

    @Test
    void getMonoOnStatusMitCustomException() {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(300)
        );
        Assertions
                .assertThatThrownBy(() -> clientWithMono.getMonoOnStatus())
                .isInstanceOf(IllegalArgumentException.class);

    }

}