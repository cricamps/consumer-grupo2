package com.duoc.producer.controller;

import com.duoc.producer.dto.EvaluacionResponse;
import com.duoc.producer.dto.SignoVitalRequest;
import com.duoc.producer.service.ProducerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para Productor 1 - Alertas.
 * Simula el dispositivo médico enviando lecturas de signos vitales del paciente.
 *
 * Endpoint:
 *   POST /signos-vitales/evaluar → evalúa la lectura y publica una alerta SOLO si detecta una anomalía
 */
@RestController
@RequestMapping("/signos-vitales")
public class SignosVitalesController {

    private final ProducerService producerService;

    public SignosVitalesController(ProducerService producerService) {
        this.producerService = producerService;
    }

    /**
     * Body esperado:
     * {
     *   "pacienteId": "P001",
     *   "pacienteNombre": "Juan Pérez",
     *   "frecuenciaCardiaca": 140,
     *   "presionSistolica": 180,
     *   "presionDiastolica": 110,
     *   "saturacionOxigeno": 89,
     *   "temperatura": 39.2
     * }
     */
    @PostMapping("/evaluar")
    public ResponseEntity<EvaluacionResponse> evaluar(@RequestBody SignoVitalRequest signos) {
        if (signos.getPacienteId() == null || signos.getPacienteId().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new EvaluacionResponse(false, "El campo 'pacienteId' es obligatorio", false));
        }
        EvaluacionResponse response = producerService.evaluarYPublicarSignosVitales(signos);
        return ResponseEntity.ok(response);
    }
}
