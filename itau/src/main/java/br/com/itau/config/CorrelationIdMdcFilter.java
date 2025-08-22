package br.com.itau.config;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationIdMdcFilter extends OncePerRequestFilter {
  private static final String HEADER = "X-Correlation-Id";
  private static final String MDC_KEY = "correlationId";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    String cid = Optional.ofNullable(request.getHeader(HEADER))
                         .filter(h -> !h.isBlank())
                         .orElse(UUID.randomUUID().toString());

    MDC.put(MDC_KEY, cid);
    try {
      response.setHeader(HEADER, cid);
      chain.doFilter(request, response);
    } finally {
      MDC.remove(MDC_KEY);
    }
  }
}
