package br.com.itau.config;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.com.itau.dto.ErroResponseDTO;
import br.com.itau.excpetion.DadosPessoaisExcpetion;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class ExcpetionHandler {
	
	@ExceptionHandler(DadosPessoaisExcpetion.class)
	public ResponseEntity<ErroResponseDTO> handleEmailException(DadosPessoaisExcpetion ex, HttpServletRequest request) {
		log.warn("Erro de e-mail: {}", ex.getMessage());
		ErroResponseDTO erro = new ErroResponseDTO(
				LocalDateTime.now(),
				HttpStatus.BAD_REQUEST.value(),
				HttpStatus.BAD_REQUEST.getReasonPhrase(), 
				ex.getMessage(), 
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}
	

}
