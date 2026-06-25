package com.duoc.producer.dto;

import java.time.LocalDateTime;

/**
 * Mensaje estructurado que se publica en el exchange "exchange_alertas_grupo2".
 * Este mismo "contrato" (mismos nombres de campo en JSON) lo usan
 * Consumidor 1 (Oracle) y Consumidor 2 (JSON) para deserializar.
 *
 * tipo: "ALERTA"  -> generado por Productor 1 cuando detecta una anomalía
 *       "RESUMEN" -> generado por Productor 2 cada cierto tiempo (registro histórico)
 */
public class MensajeColaDTO {

    private String tipo;
    private String pacienteId;
    private String pacienteNombre;
    private String descripcion;
    private String detalle;
    private LocalDateTime fechaGeneracion;

    public MensajeColaDTO() {}

    public MensajeColaDTO(String tipo, String pacienteId, String pacienteNombre, String descripcion, String detalle) {
        this.tipo = tipo;
        this.pacienteId = pacienteId;
        this.pacienteNombre = pacienteNombre;
        this.descripcion = descripcion;
        this.detalle = detalle;
        this.fechaGeneracion = LocalDateTime.now();
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public LocalDateTime getFechaGeneracion() { return fechaGeneracion; }
    public void setFechaGeneracion(LocalDateTime fechaGeneracion) { this.fechaGeneracion = fechaGeneracion; }
}
