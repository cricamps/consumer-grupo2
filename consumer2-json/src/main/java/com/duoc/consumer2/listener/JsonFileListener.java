package com.duoc.consumer2.listener;

import com.duoc.consumer2.dto.MensajeColaDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

/**
 * Consumidor 2 - "JSON".
 * Escucha la cola "cola_alertas_json_grupo2" (alimentada por el mismo
 * exchange fanout que escucha Consumidor 1) y, cuando el mensaje es una
 * ALERTA, genera un archivo .json en el sistema de archivos del hospital
 * para fines de auditoría y cumplimiento normativo.
 *
 * Los mensajes de tipo RESUMEN se reciben pero no generan archivo
 * (el caso solo pide auditar alertas en formato .json).
 */
@Component
public class JsonFileListener {

    private static final Logger log = LoggerFactory.getLogger(JsonFileListener.class);
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmssSSS");

    private final ObjectMapper objectMapper;

    @Value("${app.alertas.output-dir}")
    private String outputDir;

    public JsonFileListener() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.findAndRegisterModules(); // soporte para LocalDateTime
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.json}")
    public void recibirMensaje(MensajeColaDTO mensaje) {
        log.info("===========================================");
        log.info("[CONSUMIDOR 2 - JSON] Mensaje recibido (tipo={}): {}", mensaje.getTipo(), mensaje.getDescripcion());
        log.info("===========================================");

        if (!"ALERTA".equalsIgnoreCase(mensaje.getTipo())) {
            log.info("Mensaje de tipo '{}' recibido; no se genera archivo .json (solo se auditan alertas).", mensaje.getTipo());
            return;
        }

        try {
            File dir = new File(outputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String nombreArchivo = String.format("alerta_%s_%s.json",
                    safe(mensaje.getPacienteId()),
                    java.time.LocalDateTime.now().format(TS_FORMAT));

            Path destino = Path.of(outputDir, nombreArchivo);
            objectMapper.writeValue(destino.toFile(), mensaje);

            log.info("Archivo JSON generado con éxito en: {}", destino.toAbsolutePath());
        } catch (IOException e) {
            log.error("Error al generar el archivo JSON para la alerta del paciente {}: {}",
                    mensaje.getPacienteId(), e.getMessage(), e);
        }
    }

    private String safe(String valor) {
        return valor == null ? "SIN_ID" : valor.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}
