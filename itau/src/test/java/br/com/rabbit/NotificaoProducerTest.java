package br.com.rabbit;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.rabbit.NotificaoProducer;
import static org.junit.jupiter.api.Assertions.*;

class NotificaoProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private NotificaoProducer producer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // simula @Value dos campos
        ReflectionTestUtils.setField(producer, "delayExchange", "notificacao.delay.exchange");
        ReflectionTestUtils.setField(producer, "delayRoutingKey", "notificacao.delay.routing");
    }

    @Test
    @DisplayName("Deve enviar mensagem com TTL de 120000 ms (2 minutos)")
    void enviarParaFilaComAtraso_aplicaTTL() {
        // arrange
        var dto = new DadosPessoaisDTO(
            "Ana", "Silva", 28, "BR", "ana@mail.com", "PENDENTE"
        );

        ArgumentCaptor<MessagePostProcessor> mppCaptor = ArgumentCaptor.forClass(MessagePostProcessor.class);

        // act
        producer.enviarParaFilaComAtraso(dto);

        // assert 1: chamada ao convertAndSend com exchange/routing/dto corretos
        verify(rabbitTemplate, times(1))
            .convertAndSend(
                eq("notificacao.delay.exchange"),
                eq("notificacao.delay.routing"),
                eq(dto),
                mppCaptor.capture()
            );

        // assert 2: aplica o MessagePostProcessor e valida o TTL
        MessageProperties props = new MessageProperties();
        Message original = new Message(new byte[0], props);

        Message processed = mppCaptor.getValue().postProcessMessage(original);

        assertNotNull(processed);
        assertEquals("120000", processed.getMessageProperties().getExpiration(),
            "TTL esperado é 120000 ms (2 minutos)");
    }
}