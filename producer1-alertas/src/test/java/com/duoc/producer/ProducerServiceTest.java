package com.duoc.producer;

import com.duoc.producer.dto.AlertaRequest;
import com.duoc.producer.dto.MensajeColaDTO;
import com.duoc.producer.dto.MensajeRequest;
import com.duoc.producer.dto.PublicacionResponse;
import com.duoc.producer.dto.SignoVitalRequest;
import com.duoc.producer.dto.EvaluacionResponse;
import com.duoc.producer.model.MensajePublicado;
import com.duoc.producer.repository.MensajePublicadoRepository;
import com.duoc.producer.service.EvaluadorSignosVitalesService;
import com.duoc.producer.service.ProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del ProducerService (Productor 1 - Alertas).
 * Verifica que los mensajes se publican en el exchange fanout
 * y se registran correctamente en Oracle.
 */
@ExtendWith(MockitoExtension.class)
class ProducerServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private MensajePublicadoRepository repository;

    @Mock
    private EvaluadorSignosVitalesService evaluador;

    @InjectMocks
    private ProducerService producerService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(producerService, "exchangeName", "exchange_alertas_grupo2");
    }

    @Test
    void evaluarYPublicarSignosVitales_conAnomalia_debePublicarAlerta() {
        SignoVitalRequest signos = new SignoVitalRequest();
        signos.setPacienteId("P001");
        signos.setPacienteNombre("Juan Pérez");
        signos.setFrecuenciaCardiaca(140);
        signos.setPresionSistolica(180);
        signos.setPresionDiastolica(110);
        signos.setSaturacionOxigeno(89);
        signos.setTemperatura(39.2);

        when(evaluador.evaluar(signos)).thenReturn("Frecuencia cardíaca anormal: 140 bpm");
        when(repository.save(any(MensajePublicado.class))).thenReturn(new MensajePublicado());

        EvaluacionResponse response = producerService.evaluarYPublicarSignosVitales(signos);

        assertTrue(response.isAnomaliaDetectada());
        assertTrue(response.isAlertaPublicada());
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("exchange_alertas_grupo2"), eq(""), any(MensajeColaDTO.class));
        verify(repository, times(1)).save(any(MensajePublicado.class));
    }

    @Test
    void evaluarYPublicarSignosVitales_sinAnomalia_noDebePublicarNada() {
        SignoVitalRequest signos = new SignoVitalRequest();
        signos.setPacienteId("P002");
        when(evaluador.evaluar(signos)).thenReturn(null);

        EvaluacionResponse response = producerService.evaluarYPublicarSignosVitales(signos);

        assertFalse(response.isAnomaliaDetectada());
        assertFalse(response.isAlertaPublicada());
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(MensajeColaDTO.class));
        verify(repository, never()).save(any(MensajePublicado.class));
    }

    @Test
    void publicarAlertaMedica_debePublicarEnExchangeYGuardarEnOracle() {
        AlertaRequest request = new AlertaRequest();
        request.setPaciente("María González");
        request.setTipoAlerta("Frecuencia cardiaca elevada");
        request.setDescripcion("FC: 140 bpm");
        request.setNivelUrgencia("ALTO");

        when(repository.save(any(MensajePublicado.class))).thenReturn(new MensajePublicado());

        PublicacionResponse response = producerService.publicarAlertaMedica(request);

        assertTrue(response.isExito());
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("exchange_alertas_grupo2"), eq(""), any(MensajeColaDTO.class));
        verify(repository, times(1)).save(any(MensajePublicado.class));
    }

    @Test
    void publicarMensajeGenerico_debePublicarEnExchangeYGuardarEnOracle() {
        MensajeRequest request = new MensajeRequest("Mensaje de prueba");
        when(repository.save(any(MensajePublicado.class))).thenReturn(new MensajePublicado());

        PublicacionResponse response = producerService.publicarMensajeGenerico(request);

        assertTrue(response.isExito());
        assertEquals("Mensaje de prueba", response.getContenidoPublicado());
        verify(rabbitTemplate, times(1))
                .convertAndSend(eq("exchange_alertas_grupo2"), eq(""), any(MensajeColaDTO.class));
        verify(repository, times(1)).save(any(MensajePublicado.class));
    }

    @Test
    void listarPublicados_debeRetornarRegistrosDeOracle() {
        MensajePublicado m1 = new MensajePublicado();
        MensajePublicado m2 = new MensajePublicado();
        when(repository.findAll()).thenReturn(List.of(m1, m2));

        List<MensajePublicado> result = producerService.listarPublicados();

        assertEquals(2, result.size());
        verify(repository, times(1)).findAll();
    }
}
