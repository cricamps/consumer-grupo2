package com.duoc.consumer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Registro histórico de mensajes recibidos por Consumidor 1.
 * Tabla: HISTORIAL_SIGNOS_GRUPO2
 *
 * Guarda tanto alertas (tipo=ALERTA, generadas por Productor 1)
 * como resúmenes periódicos (tipo=RESUMEN, generados por Productor 2),
 * dando un historial completo para el análisis del personal médico.
 */
@Entity
@Table(name = "HISTORIAL_SIGNOS_GRUPO2")
public class HistorialSigno {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_historial")
    @SequenceGenerator(name = "seq_historial", sequenceName = "SEQ_HISTORIAL_SIGNOS", allocationSize = 1)
    private Long id;

    @Column(name = "TIPO")
    private String tipo;

    @Column(name = "PACIENTE_ID")
    private String pacienteId;

    @Column(name = "PACIENTE_NOMBRE")
    private String pacienteNombre;

    @Column(name = "DESCRIPCION", length = 2000)
    private String descripcion;

    @Column(name = "DETALLE", length = 2000)
    private String detalle;

    @Column(name = "FECHA_GENERACION")
    private LocalDateTime fechaGeneracion;

    @Column(name = "FECHA_RECEPCION")
    private LocalDateTime fechaRecepcion;

    @Column(name = "COLA")
    private String cola;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public String getCola() { return cola; }
    public void setCola(String cola) { this.cola = cola; }
}
