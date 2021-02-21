package de.agmyrek.webclient.client;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Configuration
public class ClientProperties {

    private String baseUrl;
    private String relativeUrl;

    URI getUrl(){
        return UriComponentsBuilder
                .fromHttpUrl(baseUrl + relativeUrl)
                .build()
                .toUri();
    }
}
