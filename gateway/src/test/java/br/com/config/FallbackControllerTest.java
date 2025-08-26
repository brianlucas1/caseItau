package br.com.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import br.com.gateway.config.FallbackController;

@WebFluxTest(FallbackController.class)
class FallbackControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void fallbackShouldReturnExpectedJson() {
        webTestClient.get()
            .uri("/__fallback")
            .exchange()
            .expectStatus().isOk()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.message").isEqualTo("Serviço indisponível. Tente novamente mais tarde.")
            .jsonPath("$.status").isEqualTo(HttpStatus.SERVICE_UNAVAILABLE.value())
            .jsonPath("$.code").isEqualTo("GATEWAY_FALLBACK");
    }
}