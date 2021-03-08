package de.agmyrek.webclient.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;


@SpringBootTest
class ClientWithMonoTest {

    @Autowired
    private ClientWithMono clientWithMono;
    static MockWebServer mockWebServer = new MockWebServer();

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) throws IOException {
        //konfiguration mit mockwebserver überschreiben
        r.add("client.uri", () -> "http://localhost:" + mockWebServer.getPort());
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
    @ValueSource(ints = { 400, 500})
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