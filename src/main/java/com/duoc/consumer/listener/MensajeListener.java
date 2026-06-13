package com.duoc.consumer.listener;

import com.duoc.consumer.model.MensajeCola;
import com.duoc.consumer.repository.MensajeColaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MensajeListener {

    private static final Logger log = LoggerFactory.getLogger(MensajeListener.class);

    @Autowired
    private MensajeColaRepository repository;

    @Value("${app.rabbitmq.queue}")
    private String queueName;

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void recibirMensaje(String mensaje) {
        log.info("===========================================");
        log.info("Mensaje recibido desde la cola: {}", mensaje);
        log.info("===========================================");

        MensajeCola registro = new MensajeCola();
        registro.setContenido(mensaje);
        registro.setFechaRecepcion(LocalDateTime.now());
        registro.setCola(queueName);

        repository.save(registro);
        log.info("Mensaje guardado en Oracle con exito.");
    }
}
