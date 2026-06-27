package com.duoc.consumer2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador auxiliar para validar (vía Postman) los archivos .json
 * que Consumidor 2 va generando a partir de las alertas.
 *
 *   GET    /alertas-json/listar          → lista los archivos generados
 *   GET    /alertas-json/{nombreArchivo} → devuelve el contenido de un archivo
 *   DELETE /alertas-json/{nombreArchivo} → elimina un archivo de auditoría
 */
@RestController
@RequestMapping("/alertas-json")
public class AlertasJsonController {

    @Value("${app.alertas.output-dir}")
    private String outputDir;

    @GetMapping("/listar")
    public ResponseEntity<List<String>> listar() {
        File dir = new File(outputDir);
        if (!dir.exists()) {
            return ResponseEntity.ok(List.of());
        }
        File[] archivos = dir.listFiles((d, nombre) -> nombre.endsWith(".json"));
        List<String> nombres = archivos == null
                ? List.of()
                : Arrays.stream(archivos).map(File::getName).collect(Collectors.toList());
        return ResponseEntity.ok(nombres);
    }

    @GetMapping(value = "/{nombreArchivo}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> verArchivo(@PathVariable String nombreArchivo) throws Exception {
        Path ruta = Path.of(outputDir, nombreArchivo);
        if (!Files.exists(ruta)) {
            return ResponseEntity.notFound().build();
        }
        String contenido = Files.readString(ruta);
        return ResponseEntity.ok(contenido);
    }

    /**
     * Elimina un archivo de auditoría .json ya procesado.
     * Permite a personal autorizado depurar archivos antiguos
     * una vez cumplido su ciclo de retención/auditoría.
     */
    @DeleteMapping("/{nombreArchivo}")
    public ResponseEntity<Void> eliminarArchivo(@PathVariable String nombreArchivo) throws Exception {
        Path ruta = Path.of(outputDir, nombreArchivo);
        if (!Files.exists(ruta)) {
            return ResponseEntity.notFound().build();
        }
        Files.delete(ruta);
        return ResponseEntity.noContent().build();
    }
}
