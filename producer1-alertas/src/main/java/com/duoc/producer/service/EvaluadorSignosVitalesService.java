package com.duoc.producer.service;

import com.duoc.producer.dto.SignoVitalRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Evalúa una lectura de signos vitales contra rangos clínicos normales.
 * Si encuentra algún valor fuera de rango, devuelve la descripción de la anomalía.
 * Si todo está dentro de rango, devuelve null (no hay alerta que publicar).
 */
@Service
public class EvaluadorSignosVitalesService {

    private static final int FC_MIN = 60;
    private static final int FC_MAX = 100;
    private static final int PAS_MIN = 90;
    private static final int PAS_MAX = 140;
    private static final int PAD_MIN = 60;
    private static final int PAD_MAX = 90;
    private static final int SPO2_MIN = 95;
    private static final double TEMP_MIN = 36.0;
    private static final double TEMP_MAX = 37.5;

    public String evaluar(SignoVitalRequest s) {
        List<String> anomalias = new ArrayList<>();

        if (s.getFrecuenciaCardiaca() != null &&
                (s.getFrecuenciaCardiaca() < FC_MIN || s.getFrecuenciaCardiaca() > FC_MAX)) {
            anomalias.add(String.format("Frecuencia cardíaca anormal: %d bpm (rango normal %d-%d)",
                    s.getFrecuenciaCardiaca(), FC_MIN, FC_MAX));
        }

        if (s.getPresionSistolica() != null &&
                (s.getPresionSistolica() < PAS_MIN || s.getPresionSistolica() > PAS_MAX)) {
            anomalias.add(String.format("Presión sistólica anormal: %d mmHg (rango normal %d-%d)",
                    s.getPresionSistolica(), PAS_MIN, PAS_MAX));
        }

        if (s.getPresionDiastolica() != null &&
                (s.getPresionDiastolica() < PAD_MIN || s.getPresionDiastolica() > PAD_MAX)) {
            anomalias.add(String.format("Presión diastólica anormal: %d mmHg (rango normal %d-%d)",
                    s.getPresionDiastolica(), PAD_MIN, PAD_MAX));
        }

        if (s.getSaturacionOxigeno() != null && s.getSaturacionOxigeno() < SPO2_MIN) {
            anomalias.add(String.format("Saturación de oxígeno baja: %d%% (mínimo normal %d%%)",
                    s.getSaturacionOxigeno(), SPO2_MIN));
        }

        if (s.getTemperatura() != null &&
                (s.getTemperatura() < TEMP_MIN || s.getTemperatura() > TEMP_MAX)) {
            anomalias.add(String.format("Temperatura anormal: %.1f°C (rango normal %.1f-%.1f°C)",
                    s.getTemperatura(), TEMP_MIN, TEMP_MAX));
        }

        if (anomalias.isEmpty()) {
            return null;
        }
        return String.join(" | ", anomalias);
    }
}
