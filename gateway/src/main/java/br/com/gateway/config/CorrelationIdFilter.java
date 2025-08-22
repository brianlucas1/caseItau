package br.com.gateway.config;


import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;


@Component
public class CorrelationIdFilter  {

	  private static final String HEADER = "X-Correlation-Id";
	  private static final String MDC_KEY = "correlationId";

	  @Bean
	  public GlobalFilter correlationIdGlobalFilter() {
	    return (exchange, chain) -> {
	      // lê ou gera o correlation id
	      String cid = exchange.getRequest().getHeaders().getFirst(HEADER);
	      if (cid == null || cid.isBlank()) {
	        cid = UUID.randomUUID().toString();
	      }

	      // coloca no request e também no MDC (logs do gateway)
	      ServerHttpRequest mutated = exchange.getRequest().mutate()
	          .header(HEADER, cid)
	          .build();

	      MDC.put(MDC_KEY, cid);

	      return chain.filter(exchange.mutate().request(mutated).build())
	          .doFinally(signalType -> MDC.remove(MDC_KEY));
	    };
	  }
}
