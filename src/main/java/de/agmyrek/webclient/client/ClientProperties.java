package de.agmyrek.webclient.client;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@ConfigurationProperties(prefix = "client")
@ConstructorBinding
@RequiredArgsConstructor
public class ClientProperties {

    private final String uri;

    URI getUri(){
        return UriComponentsBuilder.fromHttpUrl(uri).build().toUri();
    }
}
