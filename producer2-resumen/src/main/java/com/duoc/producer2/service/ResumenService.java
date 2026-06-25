package com.duoc.producer2.service;

import com.duoc.producer2.dto.MensajeColaDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

/**
 * Productor 2 - "Resumen periódico".
 *
 * Simula el envío, cada cierto tiempo, de un resumen de señales vitales
 * de los pacientes monitoreados (para registro histórico), publicándolo
 * en el mismo exchange fanout que usa Productor 1.
 *
 * En un hospital real este resumen vendría agregado desde los dispositivos
 * médicos; aquí se simula con valores aleatorios dentro de un rango amplio
 * para efectos de la demostración.
 */
@Service
public class ResumenService {

    private static final List<String[]> PACIENTES_MONITOREADOS = List.of(
            new String[]{"P001", "Juan Pérez"},
            new String[]{"P002", "María González"},
            new String[]{"P003", "Carlos Soto"}
    );

    private final RabbitTemplate rabbitTemplate;
    private final Random random = new Random();

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    public ResumenService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Se ejecuta automáticamente cada 5 minutos (300000 ms).
     */
    @Scheduled(fixedRate = 300000)
    public void generarResumenAutomatico() {
        int publicados = generarYPublicarResumen();
        System.out.println("[PRODUCTOR 2] Resumen periódico automático: " + publicados + " mensajes publicados.");
    }

    /**
     * Disparo manual (para pruebas con Postman sin esperar los 5 minutos).
     */
    public int generarResumenManual() {
        int publicados = generarYPublicarResumen();
        System.out.println("[PRODUCTOR 2] Resumen periódico manual: " + publicados + " mensajes publicados.");
        return publicados;
    }

    private int generarYPublicarResumen() {
        int contador = 0;
        for (String[] paciente : PACIENTES_MONITOREADOS) {
            String pacienteId = paciente[0];
            String pacienteNombre = paciente[1];

            int fc = 65 + random.nextInt(20);          // 65-84 bpm (rango normal simulado)
            int spo2 = 96 + random.nextInt(4);          // 96-99 %
            double temp = 36.2 + random.nextDouble();   // 36.2-37.2 °C

            String detalle = String.format("FC: %d bpm | SpO2: %d%% | Temp: %.1f°C", fc, spo2, temp);
            String descripcion = "Resumen periódico de signos vitales para " + pacienteNombre;

            MensajeColaDTO mensaje = new MensajeColaDTO("RESUMEN", pacienteId, pacienteNombre, descripcion, detalle);
            rabbitTemplate.convertAndSend(exchangeName, "", mensaje);
            contador++;
        }
        return contador;
    }
}
