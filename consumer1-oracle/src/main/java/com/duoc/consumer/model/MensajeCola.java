package com.duoc.consumer.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJES_COLA")
public class MensajeCola {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_mensajes")
    @SequenceGenerator(name = "seq_mensajes", sequenceName = "SEQ_MENSAJES_COLA", allocationSize = 1)
    private Long id;

    @Column(name = "CONTENIDO", length = 4000)
    private String contenido;

    @Column(name = "FECHA_RECEPCION")
    private LocalDateTime fechaRecepcion;

    @Column(name = "COLA")
    private String cola;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public LocalDateTime getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(LocalDateTime fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public String getCola() { return cola; }
    public void setCola(String cola) { this.cola = cola; }
}
