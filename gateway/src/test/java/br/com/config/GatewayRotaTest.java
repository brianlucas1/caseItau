package br.com.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;

@SpringBootTest(properties = "TARGET_API_URI=http://example.org")
class GatewayRotaTest {

    @Autowired
    private RouteLocator routeLocator;

    @Test
    void itauApiRouteConfiguredCorrectly() {
        List<Route> routes = routeLocator.getRoutes().collectList().block();
        Route route = routes.stream()
                .filter(r -> "itau-api".equals(r.getId()))
                .findFirst()
                .orElse(null);

        assertThat(route).as("Route itau-api should exist").isNotNull();
        assertThat(route.getPredicate().toString()).contains("/api/**");
        assertThat(route.getUri().toString()).isEqualTo("http://example.org");

        String filters = route.getFilters().toString();
        assertThat(filters).contains("CircuitBreaker");
        assertThat(filters).contains("X-Forwarded-By");
    }
}
