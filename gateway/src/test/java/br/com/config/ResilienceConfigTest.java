package br.com.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;

import br.com.gateway.config.ResilienceConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;

@SpringBootTest(classes = ResilienceConfig.class)
class ResilienceConfigTest {

  @Autowired
  private ReactiveResilience4JCircuitBreakerFactory factory;

  @Test
  void shouldConfigureCircuitBreakerFactory() {
    // trigger creation of circuit breaker with configured settings
    factory.create("apiCB");

    TimeLimiterConfig timeLimiterConfig = factory.getTimeLimiterRegistry()
        .find("apiCB")
        .orElseThrow()
        .getTimeLimiterConfig();

    CircuitBreakerConfig circuitBreakerConfig = factory.getCircuitBreakerRegistry()
        .circuitBreaker("apiCB")
        .getCircuitBreakerConfig();

    assertEquals(Duration.ofSeconds(2), timeLimiterConfig.getTimeoutDuration());
    assertEquals(10, circuitBreakerConfig.getSlidingWindowSize());
    assertEquals(50f, circuitBreakerConfig.getFailureRateThreshold());
  }
}