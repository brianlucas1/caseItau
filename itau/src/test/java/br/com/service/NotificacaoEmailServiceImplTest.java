package br.com.service;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.service.NotificacaoEmailServiceImpl;

@ExtendWith(OutputCaptureExtension.class)
class NotificacaoEmailServiceImplTest {

    private final NotificacaoEmailServiceImpl service = new NotificacaoEmailServiceImpl();

    @Test
    @DisplayName("Deve montar corpo e registrar log de sucesso")
    void enviarEmailCadastroPendente_ok(CapturedOutput output) {
        // arrange
        var dto = new DadosPessoaisDTO(
            "Ana", "Silva", 28, "BR", "ana@mail.com", "PENDENTE"
        );

        // act
        service.enviarEmailCadastroPendente(dto);

        // assert: valida saída no console e log
        assertThat(output.getOut())
            .contains("E-mail enviado com sucesso")
            .contains("Olá, Ana")
            .contains("Resumo: Ana Silva, 28 anos, BR");

        // o @Slf4j também cai no output capturado
        assertThat(output.getOut() + output.getErr())
            .contains("Email de pendencia recebido com sucesso");
    }

    @Test
    @DisplayName("Deve encapsular exceções em RuntimeException")
    void enviarEmailCadastroPendente_erro(CapturedOutput output) {
        // arrange: passar null dispara NPE dentro do try -> capturado e reempacotado
        DadosPessoaisDTO dtoNull = null;

        // act + assert
        assertThatThrownBy(() -> service.enviarEmailCadastroPendente(dtoNull))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Falha ao enviar e-mail");

        // opcional: verificar log de erro
        assertThat(output.getOut() + output.getErr())
            .contains("Fala ao enviar o e-mail de cadastro pendente");
    }
}