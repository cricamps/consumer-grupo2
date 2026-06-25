package com.duoc.producer.config;

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
 * Configuración de RabbitMQ - Sumativa S6 (Experiencia 2).
 *
 * Topología: un exchange tipo FANOUT reparte cada mensaje (alerta o resumen)
 * a DOS colas independientes, una por cada consumidor:
 *
 *   Productor 1 (alertas) ──┐
 *   Productor 2 (resumen) ──┴─→ exchange_alertas_grupo2 (fanout)
 *                              ├─→ cola_alertas_oracle_grupo2 → Consumidor 1 (Oracle)
 *                              └─→ cola_alertas_json_grupo2   → Consumidor 2 (JSON)
 *
 * Esto permite que CADA mensaje publicado llegue, de forma independiente,
 * tanto al consumidor que persiste en Oracle como al que genera el archivo .json.
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
