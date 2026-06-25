package com.duoc.consumer.listener;

import com.duoc.consumer.dto.MensajeColaDTO;
import com.duoc.consumer.model.HistorialSigno;
import com.duoc.consumer.repository.HistorialSignoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Consumidor 1 - "Oracle".
 * Escucha la cola "cola_alertas_oracle_grupo2" (alimentada por el exchange
 * fanout) y persiste TODO lo que llega (alertas y resúmenes) en Oracle,
 * dando un registro histórico completo para el personal médico.
 */
@Component
public class MensajeListener {

    private static final Logger log = LoggerFactory.getLogger(MensajeListener.class);

    @Autowired
    private HistorialSignoRepository repository;

    @Value("${app.rabbitmq.queue.oracle}")
    private String colaOracle;

    @RabbitListener(queues = "${app.rabbitmq.queue.oracle}")
    public void recibirMensaje(MensajeColaDTO mensaje) {
        log.info("===========================================");
        log.info("[CONSUMIDOR 1 - ORACLE] Mensaje recibido (tipo={}): {}", mensaje.getTipo(), mensaje.getDescripcion());
        log.info("===========================================");

        HistorialSigno registro = new HistorialSigno();
        registro.setTipo(mensaje.getTipo());
        registro.setPacienteId(mensaje.getPacienteId());
        registro.setPacienteNombre(mensaje.getPacienteNombre());
        registro.setDescripcion(mensaje.getDescripcion());
        registro.setDetalle(mensaje.getDetalle());
        registro.setFechaGeneracion(mensaje.getFechaGeneracion());
        registro.setFechaRecepcion(LocalDateTime.now());
        registro.setCola(colaOracle);

        repository.save(registro);
        log.info("Mensaje guardado en Oracle (tabla HISTORIAL_SIGNOS_GRUPO2) con exito.");
    }
}
