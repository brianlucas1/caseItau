package br.com.config;


import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import br.com.itau.config.CorrelationIdMdcFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

class CorrelationIdMdcFilterTest {

    private final CorrelationIdMdcFilter filter = new CorrelationIdMdcFilter();

    @Test
    @DisplayName("Deve propagar o X-Correlation-Id do request para MDC e response")
    void devePropagarCorrelationIdExistente() throws ServletException, IOException {
        // arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        String cid = UUID.randomUUID().toString();
        request.addHeader("X-Correlation-Id", cid);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();

        // act
        filter.doFilter(request, response, chain);

        // assert
        assertThat(response.getHeader("X-Correlation-Id")).isEqualTo(cid);
        assertThat(MDC.get("correlationId")).isNull(); // limpo no finally
    }

    @Test
    @DisplayName("Deve gerar novo CorrelationId quando header não existe")
    void deveGerarCorrelationIdNovo() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = new MockFilterChain();

        filter.doFilter(request, response, chain);

        String cid = response.getHeader("X-Correlation-Id");
        assertThat(cid).isNotBlank();
        assertThat(UUID.fromString(cid)).isInstanceOf(UUID.class); // valida formato
        assertThat(MDC.get("correlationId")).isNull(); // limpo no finally
    }
}