package com.duoc.producer2.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Misma topología que declara Productor 1: un exchange fanout
 * que reparte a las dos colas (Oracle y JSON). Declarar los mismos
 * beans aquí es idempotente (RabbitMQ no falla si ya existen con
 * la misma configuración) y hace que este microservicio pueda
 * arrancar de forma independiente sin depender de que Productor 1
 * haya arrancado primero.
 */
@Configuration
public class RabbitMQConfig {

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    @Value("${app.rabbitmq.queue.oracle}")
    private String colaOracle;

    @Value("${app.rabbitmq.queue.json}")
    private String colaJson;

    @Bean
    public FanoutExchange alertasExchange() {
        return new FanoutExchange(exchangeName, true, false);
    }

    @Bean
    public Queue colaOracleQueue() {
        return new Queue(colaOracle, true);
    }

    @Bean
    public Queue colaJsonQueue() {
        return new Queue(colaJson, true);
    }

    @Bean
    public Binding bindingOracle(Queue colaOracleQueue, FanoutExchange alertasExchange) {
        return BindingBuilder.bind(colaOracleQueue).to(alertasExchange);
    }

    @Bean
    public Binding bindingJson(Queue colaJsonQueue, FanoutExchange alertasExchange) {
        return BindingBuilder.bind(colaJsonQueue).to(alertasExchange);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
