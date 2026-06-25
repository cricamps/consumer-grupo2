package com.duoc.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Consumidor 1 (Oracle) declara la misma topología que los productores
 * (exchange fanout + 2 colas) para poder arrancar de forma independiente,
 * y registra el MessageConverter JSON para que @RabbitListener pueda
 * recibir directamente objetos MensajeColaDTO en vez de Strings.
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
}
