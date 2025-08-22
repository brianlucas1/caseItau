package br.com.gateway.config;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

	
	 @RequestMapping(path = "/__fallback", produces = MediaType.APPLICATION_JSON_VALUE)
	  public Map<String, Object> fallback() {
	    return Map.of(
	      "message", "Serviço indisponível. Tente novamente mais tarde.",
	      "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
	      "code", "GATEWAY_FALLBACK"
	    );
	  }
}
