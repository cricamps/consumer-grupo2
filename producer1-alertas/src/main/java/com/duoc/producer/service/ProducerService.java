package com.duoc.producer.service;

import com.duoc.producer.dto.AlertaRequest;
import com.duoc.producer.dto.EvaluacionResponse;
import com.duoc.producer.dto.MensajeColaDTO;
import com.duoc.producer.dto.MensajeRequest;
import com.duoc.producer.dto.PublicacionResponse;
import com.duoc.producer.dto.SignoVitalRequest;
import com.duoc.producer.model.MensajePublicado;
import com.duoc.producer.repository.MensajePublicadoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Productor 1 - "Alertas".
 * Publica mensajes en el exchange fanout "exchange_alertas_grupo2",
 * desde donde se reparten automáticamente a las dos colas
 * (cola_alertas_oracle_grupo2 y cola_alertas_json_grupo2).
 *
 * Cada publicación también se registra localmente en Oracle
 * (tabla MENSAJES_PUBLICADOS) como bitácora de auditoría del productor.
 */
@Service
public class ProducerService {

    private final RabbitTemplate rabbitTemplate;
    private final MensajePublicadoRepository repository;
    private final EvaluadorSignosVitalesService evaluador;

    @Value("${app.rabbitmq.exchange}")
    private String exchangeName;

    public ProducerService(RabbitTemplate rabbitTemplate,
                            MensajePublicadoRepository repository,
                            EvaluadorSignosVitalesService evaluador) {
        this.rabbitTemplate = rabbitTemplate;
        this.repository = repository;
        this.evaluador = evaluador;
    }

    /**
     * Caso principal de Productor 1: recibe una lectura de signos vitales,
     * evalúa si hay una anomalía y, de ser así, publica una alerta.
     */
    public EvaluacionResponse evaluarYPublicarSignosVitales(SignoVitalRequest signos) {
        String descripcionAnomalia = evaluador.evaluar(signos);

        if (descripcionAnomalia == null) {
            return new EvaluacionResponse(false, "Señales dentro de rangos normales. No se publicó alerta.", false);
        }

        MensajeColaDTO mensaje = new MensajeColaDTO(
                "ALERTA",
                signos.getPacienteId(),
                signos.getPacienteNombre(),
                descripcionAnomalia,
                signos.toString()
        );

        publicarEnExchange(mensaje, "ALERTA_AUTOMATICA");

        return new EvaluacionResponse(true, descripcionAnomalia, true);
    }

    /**
     * Publica una alerta médica armada manualmente (útil para pruebas con Postman).
     */
    public PublicacionResponse publicarAlertaMedica(AlertaRequest request) {
        MensajeColaDTO mensaje = new MensajeColaDTO(
                "ALERTA",
                "N/A",
                request.getPaciente(),
                request.getTipoAlerta() + " - " + request.getDescripcion() + " (Urgencia: " + request.getNivelUrgencia() + ")",
                request.toString()
        );

        publicarEnExchange(mensaje, "ALERTA_MEDICA");

        return new PublicacionResponse(true, "Alerta médica publicada exitosamente en el exchange", request.toString());
    }

    /**
     * Publica un mensaje de texto libre (se mantiene por compatibilidad con pruebas previas).
     */
    public PublicacionResponse publicarMensajeGenerico(MensajeRequest request) {
        MensajeColaDTO mensaje = new MensajeColaDTO(
                "GENERICO",
                "N/A",
                "N/A",
                request.getMensaje(),
                request.getMensaje()
        );

        publicarEnExchange(mensaje, "GENERICO");

        return new PublicacionResponse(true, "Mensaje publicado exitosamente en el exchange", request.getMensaje());
    }

    public List<MensajePublicado> listarPublicados() {
        return repository.findAll();
    }

    private void publicarEnExchange(MensajeColaDTO mensaje, String tipoRegistro) {
        rabbitTemplate.convertAndSend(exchangeName, "", mensaje);
        System.out.println("[PRODUCTOR 1] Publicado en exchange '" + exchangeName + "': " + mensaje.getDescripcion());

        MensajePublicado registro = new MensajePublicado();
        registro.setContenido(mensaje.getDescripcion());
        registro.setTipo(tipoRegistro);
        registro.setCola(exchangeName);
        registro.setFechaPublicacion(LocalDateTime.now());
        repository.save(registro);
    }
}
