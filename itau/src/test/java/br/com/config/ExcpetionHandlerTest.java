package br.com.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import br.com.itau.config.ExcpetionHandler;
import br.com.itau.dto.ErroResponseDTO;
import br.com.itau.excpetion.DadosPessoaisExcpetion;

public class ExcpetionHandlerTest {
	

    private final ExcpetionHandler handler = new ExcpetionHandler();

    @Test
    void deveRetornarErroResponseComStatus400() {
        // arrange
        var ex = new DadosPessoaisExcpetion("Email já cadastrado");
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/dados-pessoais");

        // act
        ResponseEntity<ErroResponseDTO> response = handler.handleEmailException(ex, request);

        // assert
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(400);
        assertThat(response.getBody().path()).isEqualTo("/api/dados-pessoais");
        assertThat(response.getBody().timestamp()).isBeforeOrEqualTo(LocalDateTime.now());
    }

}
