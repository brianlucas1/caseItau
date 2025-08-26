package br.com.rabbit;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.rabbit.NotificaoConsumer;
import br.com.itau.service.NotificacaoEmailService; // ajuste o pacote conforme seu projeto

class NotificaoConsumerTest {

    @Mock
    private NotificacaoEmailService notificacaoEmailService;

    @InjectMocks
    private NotificaoConsumer consumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Simula @Value("${rabbitmq.queue:notificacao.queue}")
        ReflectionTestUtils.setField(consumer, "queueName", "notificacao.queue");
    }

    @Test
    @DisplayName("Deve consumir mensagem e delegar para NotificacaoEmailService")
    void consumirMensagem_deveChamarServico() {
        // arrange
        var dto = new DadosPessoaisDTO(
            "Ana",          // nome
            "Silva",        // sobreNome
            28,             // idade
            "BR",           // pais
            "ana@mail.com", // email
            "PENDENTE"      // status
        );

        // act
        consumer.consumirMensagem(dto);

        // assert
        verify(notificacaoEmailService, times(1))
            .enviarEmailCadastroPendente(dto);
    }

    @Test
    @DisplayName("Não deve falhar se queueName estiver com valor default")
    void consumirMensagem_naoFalhaComQueueDefault() {
        // arrange
        ReflectionTestUtils.setField(consumer, "queueName", "notificacao.queue"); // default do @Value
        var dto = new DadosPessoaisDTO("Joao", "Souza", 31, "BR", "joao@mail.com", "PENDENTE");

        // act
        consumer.consumirMensagem(dto);

        // assert
        verify(notificacaoEmailService).enviarEmailCadastroPendente(dto);
    }
}
