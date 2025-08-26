 package br.com.itau.rabbit;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import br.com.itau.dto.DadosPessoaisDTO;
import br.com.itau.service.NotificacaoEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificaoConsumer {
	 
	  private final NotificacaoEmailService notificacaoEmailService;

	  @Value("${rabbitmq.queue:notificacao.queue}")
	  private String queueName;


		@RabbitListener(queues = "${rabbitmq.queue:notificacao.queue}")
		public void consumirMensagem(DadosPessoaisDTO dto) {
			System.out.println("Menssagem recebida com sucesso na fila \"" + queueName + "\": " + dto);
			log.info("Leitura da menssagem da fila feita com sucesso.");
			notificacaoEmailService.enviarEmailCadastroPendente(dto);
		}

}
