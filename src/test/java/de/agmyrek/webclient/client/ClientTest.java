package de.agmyrek.webclient.client;

import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.io.IOException;


@SpringBootTest
class ClientTest {

    @Autowired
    private Client client;
    static MockWebServer mockWebServer = new MockWebServer();
/*
    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry r) throws IOException {
        r.add("clients.bkverwalten.base-url", () -> "http://localhost:" + mockWebServer.getPort())
    }
*/
    @Test
    void get() {
    }
}