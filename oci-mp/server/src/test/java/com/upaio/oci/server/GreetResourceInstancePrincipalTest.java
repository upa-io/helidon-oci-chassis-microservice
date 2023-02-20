package com.upaio.oci.server;

import java.net.URI;

import jakarta.inject.Inject;

import io.helidon.microprofile.server.ServerCdiExtension;
import io.helidon.microprofile.tests.junit5.Configuration;
import io.helidon.microprofile.tests.junit5.HelidonTest;

import com.upaio.oci.client.api.ApiException;
import com.upaio.oci.client.api.GreetApi;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@HelidonTest
@Configuration(profile = "prod")
class GreetResourceInstancePrincipalTest {
    private GreetApi greetApi;

    @Inject
    private ServerCdiExtension serverCdiExtension;

    @BeforeEach
    void beforeEach() {
        URI uriInfo = URI.create(String.format("http://localhost:%s/",
                                               serverCdiExtension.port()
        ));
        greetApi = RestClientBuilder.newBuilder()
                .baseUri(uriInfo)
                .build(GreetApi.class);
    }

    @Test
    void testHelloWorld() throws ApiException {
        assertThat(Common.getDefaultMessage(greetApi), is("Hello World!"));
        assertThat(Common.getMessage(greetApi, "Joe"), is("Hello Joe!"));

        // Change Greeting test
        Common.updateGreeting(greetApi, "Hola");
        assertThat(Common.getDefaultMessage(greetApi), is("Hola World!"));
    }
}
