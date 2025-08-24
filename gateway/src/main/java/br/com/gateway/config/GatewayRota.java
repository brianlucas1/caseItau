package br.com.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRota  {
	
	@Value("${TARGET_API_URI:http://localhost:8080}") 
	private String targetApiUri;

	@Bean
	  RouteLocator routes(RouteLocatorBuilder r) {

		  return r.routes()
		    .route("itau-api", p -> p
		    		 .path("/api/**")
		      .filters(f -> f
		        .circuitBreaker(c -> c.setName("apiCB").setFallbackUri("forward:/__fallback"))
		        .addRequestHeader("X-Forwarded-By", "Itau-Gateway"))
		      .uri(targetApiUri)) 
		    .build();
		}

}
