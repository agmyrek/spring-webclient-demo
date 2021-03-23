package de.agmyrek.webclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Normalerweise erbt diese Klasse noch von 'SpringBootServletInitializer'
 * Das ist bei diesem Reactive-Projekt nicht notwendig.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class Application {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
