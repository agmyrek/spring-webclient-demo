package de.agmyrek.webclient.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


class ClientWithMonoDispatcherTest {

    private static final String personRessource = "/person";
    private static final String personClientFehlerRessource = "/person-fehler";

    static ClientWithMonoPost clientWithMono;
    static MockWebServer mockWebServer;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setup(){
        mockWebServer = new MockWebServer();
        final Dispatcher dispatcher = new Dispatcher() {

            @Override
            public MockResponse dispatch (RecordedRequest request) {
                return switch (request.getPath()) {
                    case personRessource -> new MockResponse().setResponseCode(200);
                    case personClientFehlerRessource -> new MockResponse().setResponseCode(400);
                    default -> new MockResponse().setResponseCode(404);
                };
            }
        };
        mockWebServer.setDispatcher(dispatcher);
        var mockedUri = "http://localhost:" + mockWebServer.getPort();
        var clientProperties = new ClientProperties(mockedUri);
        var webclient = WebClient.builder().build();
        clientWithMono = new ClientWithMonoPost(webclient, clientProperties);
    }
    @Test
    void postMonoHappyPath() throws JsonProcessingException, InterruptedException {
        var jsonBodyRequest = toJson(
                """
                {
                    "name": "Mustermann"
                }
                """);
        Assertions
                .assertThatNoException()
                .isThrownBy(() -> clientWithMono.postMono(jsonBodyRequest, personRessource));
        //prüft, ob der Request-Payload übertragen wurde
        Assertions
                .assertThat(toJson(mockWebServer.takeRequest().getBody().readUtf8()))
                .isEqualTo(jsonBodyRequest);
    }

    @Test
    void postMonoClientFehler() throws JsonProcessingException, InterruptedException {
        var jsonBodyRequest = toJson(
               """
               {
                    "name": "Fehler"
                }
                """);
        Assertions
                .assertThatThrownBy(() -> clientWithMono.postMono(jsonBodyRequest, personClientFehlerRessource))
                .isInstanceOf(WebClientResponseException.class);;
        //prüft, ob der Request-Payload übertragen wurde
        Assertions
                .assertThat(toJson(mockWebServer.takeRequest().getBody().readUtf8()))
                .isEqualTo(jsonBodyRequest);
    }

    private JsonNode toJson(String json) throws JsonProcessingException {
        return objectMapper.readTree(json);
    }


}