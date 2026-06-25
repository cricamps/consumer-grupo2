package com.duoc.producer2.controller;

import com.duoc.producer2.service.ResumenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Controlador REST para Productor 2 - Resumen periódico.
 *
 * Endpoint:
 *   POST /resumen/enviar → dispara manualmente el envío del resumen
 *                          (la tarea automática corre cada 5 minutos vía @Scheduled)
 */
@RestController
@RequestMapping("/resumen")
public class ResumenController {

    private final ResumenService resumenService;

    public ResumenController(ResumenService resumenService) {
        this.resumenService = resumenService;
    }

    @PostMapping("/enviar")
    public ResponseEntity<Map<String, Object>> enviarResumen() {
        int publicados = resumenService.generarResumenManual();
        return ResponseEntity.ok(Map.of(
                "exito", true,
                "mensajesPublicados", publicados,
                "mensaje", "Resumen periódico publicado en el exchange"
        ));
    }
}
