package com.duoc.producer.dto;

/**
 * Respuesta del endpoint POST /signos-vitales/evaluar
 */
public class EvaluacionResponse {

    private boolean anomaliaDetectada;
    private String descripcion;
    private boolean alertaPublicada;

    public EvaluacionResponse() {}

    public EvaluacionResponse(boolean anomaliaDetectada, String descripcion, boolean alertaPublicada) {
        this.anomaliaDetectada = anomaliaDetectada;
        this.descripcion = descripcion;
        this.alertaPublicada = alertaPublicada;
    }

    public boolean isAnomaliaDetectada() { return anomaliaDetectada; }
    public void setAnomaliaDetectada(boolean anomaliaDetectada) { this.anomaliaDetectada = anomaliaDetectada; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isAlertaPublicada() { return alertaPublicada; }
    public void setAlertaPublicada(boolean alertaPublicada) { this.alertaPublicada = alertaPublicada; }
}
