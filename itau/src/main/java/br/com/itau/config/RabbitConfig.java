package br.com.itau.config;



import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
	
	@Value("${rabbitmq.exchange:notificacao.exchange}")
	private String exchange;
	@Value("${rabbitmq.queue:notificacao.queue}")
	private String queue;
	@Value("${rabbitmq.routingkey:notificacao.routing}")
	private String routingKey;

	// Delay infra
	@Value("${rabbitmq.delay.exchange:notificacao.delay.exchange}")
	private String delayExchange;
	@Value("${rabbitmq.delay.queue:notificacao.delay.queue}")
	private String delayQueue;
	@Value("${rabbitmq.delay.routingkey:notificacao.delay.routing}")
	private String delayRoutingKey;

	@Bean
	public DirectExchange notificacaoExchange() {
		return new DirectExchange(exchange);
	}

	@Bean
	public Queue notificacaoQueue() {
		return QueueBuilder.durable(queue).build();
	}

	@Bean
	public Binding notificacaoBinding() {
		return BindingBuilder.bind(notificacaoQueue()).to(notificacaoExchange()).with(routingKey);
	}

	// Delay queue -> quando a mensagem expira, vai para a exchange principal
	@Bean
	public DirectExchange delayExchange() {
		return new DirectExchange(delayExchange);
	}

	@Bean
	public Queue delayQueue() {
		return QueueBuilder.durable(delayQueue).withArgument("x-dead-letter-exchange", exchange)
				.withArgument("x-dead-letter-routing-key", routingKey).build();
	}

	@Bean
	public Binding delayBinding() {
		return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(delayRoutingKey);
	}

	@Bean
	public MessageConverter jsonMessageConverter() {
		return new Jackson2JsonMessageConverter();
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory cf, MessageConverter converter) {
		RabbitTemplate t = new RabbitTemplate(cf);
		t.setMessageConverter(converter);
		return t;
	}
}