package com.duoc.consumer.controller;

import com.duoc.consumer.model.HistorialSigno;
import com.duoc.consumer.repository.HistorialSignoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para Consumidor 1 - Oracle.
 * Permite al personal médico consultar el historial persistido y marcar
 * registros (alertas/resúmenes) como revisados, una vez analizados.
 *
 * Endpoints:
 *   GET /historial/listar       → lista todo el historial guardado en Oracle
 *   PUT /historial/{id}/revisar → marca un registro como revisado por personal médico
 */
@RestController
@RequestMapping("/historial")
public class HistorialController {

    private final HistorialSignoRepository repository;

    public HistorialController(HistorialSignoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/listar")
    public ResponseEntity<List<HistorialSigno>> listar() {
        return ResponseEntity.ok(repository.findAll());
    }

    /**
     * Marca un registro del historial (alerta o resumen) como revisado
     * por el personal médico, una vez analizado.
     */
    @PutMapping("/{id}/revisar")
    public ResponseEntity<HistorialSigno> marcarRevisado(@PathVariable Long id) {
        Optional<HistorialSigno> registroOpt = repository.findById(id);
        if (registroOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        HistorialSigno registro = registroOpt.get();
        registro.setRevisado(true);
        HistorialSigno actualizado = repository.save(registro);
        return ResponseEntity.ok(actualizado);
    }
}
