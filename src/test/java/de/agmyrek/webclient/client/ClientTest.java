package de.agmyrek.webclient.client;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;


@SpringBootTest
class ClientTest {

    @Autowired
    private Client client;
    static MockWebServer mockWebServer = new MockWebServer();

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) throws IOException {
        //konfiguration mit mockwebserver überschreiben
        r.add("client.uri", () -> "http://localhost:" + mockWebServer.getPort());
    }

    @Test
    void get() {
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
        var responseDto = client.get().block();

        Assertions.assertThat(responseDto.get("name").asText()).isEqualTo("Mustermann");


    }
}