package com.duoc.producer.dto;

/**
 * Representa la lectura de un dispositivo médico conectado a un paciente.
 * Endpoint: POST /signos-vitales/evaluar
 */
public class SignoVitalRequest {

    private String pacienteId;
    private String pacienteNombre;
    private Integer frecuenciaCardiaca;     // bpm
    private Integer presionSistolica;       // mmHg
    private Integer presionDiastolica;      // mmHg
    private Integer saturacionOxigeno;      // %
    private Double temperatura;             // °C

    public SignoVitalRequest() {}

    public String getPacienteId() { return pacienteId; }
    public void setPacienteId(String pacienteId) { this.pacienteId = pacienteId; }

    public String getPacienteNombre() { return pacienteNombre; }
    public void setPacienteNombre(String pacienteNombre) { this.pacienteNombre = pacienteNombre; }

    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }

    public Integer getPresionSistolica() { return presionSistolica; }
    public void setPresionSistolica(Integer presionSistolica) { this.presionSistolica = presionSistolica; }

    public Integer getPresionDiastolica() { return presionDiastolica; }
    public void setPresionDiastolica(Integer presionDiastolica) { this.presionDiastolica = presionDiastolica; }

    public Integer getSaturacionOxigeno() { return saturacionOxigeno; }
    public void setSaturacionOxigeno(Integer saturacionOxigeno) { this.saturacionOxigeno = saturacionOxigeno; }

    public Double getTemperatura() { return temperatura; }
    public void setTemperatura(Double temperatura) { this.temperatura = temperatura; }

    @Override
    public String toString() {
        return String.format(
            "FC: %d bpm | PA: %d/%d mmHg | SpO2: %d%% | Temp: %.1f°C",
            frecuenciaCardiaca, presionSistolica, presionDiastolica, saturacionOxigeno, temperatura);
    }
}
