package br.com.itau.rabbit;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.itau.dto.DadosPessoaisDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificaoProducer {
	
	private final RabbitTemplate rabbitTemplate;


	@Value("${rabbitmq.delay.exchange:notificacao.delay.exchange}")
	private String delayExchange;
	@Value("${rabbitmq.delay.routingkey:notificacao.delay.routing}")
	private String delayRoutingKey;

	// 2 minutos = 120000 ms
	private static final String TTL_MS = "120000";

	public void enviarParaFilaComAtraso(DadosPessoaisDTO dto) {
		rabbitTemplate.convertAndSend(delayExchange, delayRoutingKey, dto, message -> {
			message.getMessageProperties().setExpiration(TTL_MS);
			return message;
		});
		log.info("Enviado para a fila a menssagem com atraso de 2m");
	}
}
