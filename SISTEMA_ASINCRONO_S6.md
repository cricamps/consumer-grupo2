# DSY2206 — Sumativa S6 (Semana 6)
## Desarrollando sistema asíncrono con la utilización de colas

**Asignatura:** Desarrollo Cloud Native I (DSY2206)
**Experiencia:** 2 — Semana 6 (Sumativa 30%)
**Integrantes:** Grupo 2
**Caso:** Sistema de alertas médicas en tiempo real — SaludAlerta

---

## 1. Arquitectura

Un exchange RabbitMQ tipo **fanout** reparte cada mensaje a **dos colas**
independientes, una por cada microservicio consumidor:

```
Productor 1 (alertas, :8086)  ──┐
Productor 2 (resumen, :8085)  ──┴──→ exchange_alertas_grupo2 (fanout)
                                   ├──→ cola_alertas_oracle_grupo2 → Consumidor 1 (Oracle, :8082)
                                   └──→ cola_alertas_json_grupo2   → Consumidor 2 (JSON, :8084)
```

| Componente | Rol | Carpeta | Puerto |
|---|---|---|---|
| RabbitMQ | Broker de mensajes | (imagen oficial) | 5672 / 15672 (mgmt UI) |
| Productor 1 — Alertas | Recibe signos vitales, detecta anomalía, publica alerta | `producer1-alertas/` | 8086 |
| Productor 2 — Resumen | Cada 5 min (`@Scheduled`) publica resumen de signos vitales | `producer2-resumen/` | 8085 |
| Consumidor 1 — Oracle | Persiste todo (alertas + resúmenes) en Oracle Cloud | `consumer1-oracle/` | 8082 |
| Consumidor 2 — JSON | Genera un archivo `.json` por cada alerta recibida | `consumer2-json/` | 8084 |

**Tabla Oracle:** `HISTORIAL_SIGNOS_GRUPO2` (columna `TIPO` = `ALERTA` o `RESUMEN`)
**Carpeta de archivos JSON:** `C:\Medicas\alertas-json\`
**Wallet Oracle:** `C:\Medicas\Wallet_AlertasDB\` (un nivel arriba del repo, montado por docker-compose)

---

## 2. Configuración de RabbitMQ — paso a paso

1. El broker se configura como contenedor Docker (imagen `rabbitmq:3-management`),
   definido en el servicio `rabbitmq` de `docker-compose.yml`. Expone:
   - `5672` → protocolo AMQP (lo usan los microservicios)
   - `15672` → interfaz web de administración (`http://localhost:15672`, user/pass: `guest`/`guest`)

2. Cada microservicio declara, al arrancar, la siguiente topología vía
   `@Configuration` (clase `RabbitMQConfig`), de forma idempotente:
   - Un `FanoutExchange` llamado `exchange_alertas_grupo2`
   - Dos `Queue` durables: `cola_alertas_oracle_grupo2` y `cola_alertas_json_grupo2`
   - Un `Binding` de cada cola hacia el exchange

   Esto significa que **no es necesario crear nada manualmente** en la UI de
   RabbitMQ: apenas arranca cualquiera de los 4 microservicios, Spring AMQP
   crea el exchange, las colas y los bindings automáticamente.

3. Productor 1 y Productor 2 publican usando
   `rabbitTemplate.convertAndSend(exchangeName, "", mensaje)` — al ser un
   exchange fanout, la routing key (`""`) no importa: el mensaje llega a
   **ambas** colas.

4. Consumidor 1 y Consumidor 2 escuchan cada uno **su propia cola** con
   `@RabbitListener(queues = "...")`, por lo que ambos procesan,
   independientemente, cada mensaje publicado.

5. Los mensajes se serializan/deserializan automáticamente como JSON gracias
   al bean `Jackson2JsonMessageConverter` declarado en cada microservicio.

---

## 3. Cómo levantar todo con Docker Compose

```bash
cd C:\Medicas\consumer
docker-compose up --build
```

Esto construye las 5 imágenes (RabbitMQ + 4 microservicios) y las levanta
conectadas en la misma red. El wallet de Oracle se monta de solo lectura
en Productor 1 y Consumidor 1, y se copia internamente a una ruta corregida
para Linux (ver `entrypoint.sh` de cada uno). Los archivos `.json` que genere
Consumidor 2 aparecerán en `C:\Medicas\alertas-json\` en tu máquina.

Para correr un microservicio individualmente fuera de Docker (con RabbitMQ
también corriendo en Docker):
```bash
cd C:\Medicas\consumer\producer1-alertas   (o producer2-resumen / consumer1-oracle / consumer2-json)
mvn spring-boot:run
```

---

## 4. Pruebas con Postman

**Productor 1 — Alerta automática (detecta anomalía):**
```
POST http://localhost:8086/signos-vitales/evaluar
{
  "pacienteId": "P001",
  "pacienteNombre": "Juan Pérez",
  "frecuenciaCardiaca": 140,
  "presionSistolica": 180,
  "presionDiastolica": 110,
  "saturacionOxigeno": 89,
  "temperatura": 39.2
}
```

**Productor 1 — Alerta manual (formato estructurado):**
```
POST http://localhost:8086/alertas/publicar
{
  "paciente": "María González",
  "tipoAlerta": "Frecuencia cardiaca elevada",
  "descripcion": "FC: 140 bpm",
  "nivelUrgencia": "ALTO"
}
```

**Productor 2 — Disparar resumen periódico manualmente (sin esperar 5 min):**
```
POST http://localhost:8085/resumen/enviar
```

**Verificar en Oracle (vía Consumidor 1 o SQL Worksheet):**
```sql
SELECT * FROM HISTORIAL_SIGNOS_GRUPO2 ORDER BY FECHA_RECEPCION DESC;
```

**Verificar archivos JSON generados (vía Consumidor 2):**
```
GET http://localhost:8084/alertas-json/listar
GET http://localhost:8084/alertas-json/{nombreArchivo}
```
(o revisar directamente la carpeta `C:\Medicas\alertas-json\`)

**RabbitMQ Management UI:**
`http://localhost:15672` → pestaña *Exchanges* → `exchange_alertas_grupo2`
→ ver ambas colas con mensajes consumidos.

---

## 5. Trabajo colaborativo

Repositorio de este proyecto: ver historial de commits de ambos integrantes
del Grupo 2 en GitHub.
