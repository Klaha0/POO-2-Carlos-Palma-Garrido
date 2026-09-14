# SpeedFast - Semana 5 - Sincronizando procesos en sistemas concurrentes

Proyecto Java desarrollado para la asignatura **Desarrollo Orientado a Objetos II** de Duoc UC.

La aplicación simula una jornada de despacho en SpeedFast. Varios repartidores trabajan simultáneamente sobre una zona de carga compartida, mientras mecanismos de sincronización impiden que un mismo pedido sea retirado por más de un repartidor.

Como valor agregado, los pedidos poseen un **nivel de urgencia**. La zona de carga atiende primero los pedidos de urgencia alta, luego los de urgencia media y finalmente los de urgencia baja.

## Autor del proyecto

- **Nombre:** Carlos Palma Garrido.
- **Carrera:** Programación de aplicaciones.
- **Sede:** Online.

---

## Descripción del sistema

El sistema representa pedidos que llegan a una zona de carga común. Tres repartidores retiran esos pedidos y simulan sus entregas de manera concurrente.

Cada pedido comienza en estado `PENDIENTE`. Cuando un repartidor lo retira, cambia a `EN_REPARTO`; después de esperar un tiempo aleatorio para representar el traslado, pasa a `ENTREGADO`. Esto se cambió un poco respecto del enunciado porque tiene más coherencia de esta forma.

La aplicación permite:

- Crear pedidos con identificador único, dirección, estado y nivel de urgencia.
- Agregar pedidos a una zona de carga compartida.
- Atender primero los pedidos con mayor urgencia.
- Evitar que dos repartidores retiren el mismo pedido.
- Ejecutar tres repartidores simultáneamente mediante `ExecutorService`.
- Simular distintos tiempos de entrega con `Thread.sleep()`.
- Comprobar al finalizar que todos los pedidos fueron entregados.

## Principios de POO aplicados

| Principio | Dónde se aplica |
|-----------|-----------------|
| **Encapsulamiento** | Los atributos de `Pedido`, `Repartidor` y `ZonaDeCarga` son privados y se utilizan mediante constructores y métodos controlados. |
| **Abstracción** | Cada clase representa un elemento concreto del problema: pedido, repartidor o zona de carga. |
| **Responsabilidad única** | `Pedido` conserva sus datos, `ZonaDeCarga` administra el recurso compartido, `Repartidor` realiza entregas y `Main` prepara y ejecuta la simulación. |
| **Composición** | Cada `Repartidor` recibe una referencia a la misma instancia de `ZonaDeCarga`. |
| **Interfaces** | `Repartidor` implementa `Runnable`, lo que permite enviarlo como tarea a un `ExecutorService`. |
| **Enumeraciones** | `EstadoPedido` y `NivelUrgencia` restringen los valores posibles y evitan errores de escritura. |

## Clases principales

| Paquete | Clase | Rol |
|---------|-------|-----|
| `model` | `Pedido` | Mantiene el ID, la dirección, el estado y el nivel de urgencia de una entrega. |
| `model` | `EstadoPedido` | Define los estados `PENDIENTE`, `EN_REPARTO` y `ENTREGADO`. |
| `model` | `NivelUrgencia` | Define las urgencias `BAJA`, `MEDIA` y `ALTA`, asociadas a los valores 1, 2 y 3. |
| `model` | `Repartidor` | Implementa `Runnable`, retira pedidos, simula su traslado y los marca como entregados. |
| `sincronizado` | `ZonaDeCarga` | Almacena y ordena los pedidos en una cola compartida, y controla su ingreso y retiro. |
| `app` | `Main` | Crea los pedidos y repartidores, administra el `ExecutorService` y valida el resultado. |

## Concurrencia y sincronización

Los tres repartidores utilizan la misma instancia de `ZonaDeCarga`. Esta clase almacena sus pedidos en una `PriorityBlockingQueue<Pedido>`, una estructura preparada para trabajar de forma segura con varios hilos.

Además, los métodos requeridos por el enunciado están sincronizados:

```java
public synchronized void agregarPedido(Pedido pedido)
public synchronized Pedido retirarPedido()
```

`retirarPedido()` utiliza `poll()`, que extrae un solo elemento de la cola. Una vez retirado, el pedido deja de estar disponible para los demás repartidores, evitando retiros duplicados.

En `Repartidor`, el retiro, el cambio a `EN_REPARTO` y su mensaje de consola se realizan dentro del mismo bloque sincronizado. Esto permite que la salida muestre fielmente el orden de retiro. La espera que simula la entrega ocurre fuera del bloqueo, de modo que los demás repartidores pueden continuar trabajando en paralelo.

## Orden de prioridad

`ZonaDeCarga` crea la cola con un `Comparator<Pedido>`. Este comparador aplica dos reglas:

1. Un pedido con mayor nivel de urgencia se retira primero.
2. Si dos pedidos tienen la misma urgencia, se retira primero el de menor ID.

Por lo tanto, el orden general de despacho es:

```text
ALTA -> MEDIA -> BAJA
```

En la simulación actual, el orden esperado de retiro es:

```text
#2, #4, #7  -> urgencia ALTA
#3, #6, #9  -> urgencia MEDIA
#1, #5, #8  -> urgencia BAJA
```

Los retiros respetan ese orden, pero las entregas pueden terminar en otro porque cada repartidor espera un tiempo aleatorio de entre uno y cuatro segundos.

## Identificadores seguros

`Pedido` utiliza un `AtomicInteger` estático como contador compartido. La operación `incrementAndGet()` genera cada identificador de forma atómica, evitando valores duplicados incluso si en el futuro varios hilos crean pedidos simultáneamente.

Cada pedido conserva el resultado en un atributo `final int`, por lo que su ID no cambia durante la ejecución.

## Estados de un pedido

El ciclo normal de un pedido es:

```text
PENDIENTE -> EN_REPARTO -> ENTREGADO
```

| Estado | Significado |
|--------|-------------|
| `PENDIENTE` | El pedido se encuentra disponible en la zona de carga. |
| `EN_REPARTO` | Un repartidor retiró el pedido y está simulando su entrega. |
| `ENTREGADO` | El repartidor completó la entrega. |

Si un hilo es interrumpido mientras realiza una entrega, el pedido vuelve a `PENDIENTE` y se agrega nuevamente a la zona de carga antes de que el repartidor termine su ejecución.

## Administración del ExecutorService

`Main` crea un pool fijo de tres hilos:

```java
ExecutorService executor = Executors.newFixedThreadPool(3);
```

Cada repartidor se envía al executor mediante `execute()`. Después de registrar las tres tareas, `shutdown()` impide recibir trabajo nuevo y permite terminar el que ya se encuentra en ejecución.

`awaitTermination()` espera hasta un minuto por el fin de la jornada. Si se supera ese plazo o el hilo principal es interrumpido, `shutdownNow()` solicita la detención de las tareas pendientes.

Cuando los tres repartidores terminan, `Main` recorre los nueve pedidos y comprueba que todos tengan estado `ENTREGADO`. El mensaje de jornada finalizada se imprime una sola vez y únicamente después de esta comprobación.

## Manejo de errores

| Situación | Respuesta del sistema |
|-----------|-----------------------|
| Se intenta crear la cola con una capacidad inicial igual o menor que cero | `ZonaDeCarga` lanza `IllegalArgumentException`. |
| La zona de carga no tiene pedidos disponibles | `retirarPedido()` devuelve `null` y el repartidor termina su tarea. |
| Un repartidor es interrumpido durante una entrega | El pedido vuelve a `PENDIENTE`, regresa a la cola y se restaura el indicador de interrupción. |
| La jornada supera un minuto | `Main` ejecuta `shutdownNow()` e informa que se superó el tiempo máximo. |
| El hilo principal es interrumpido durante la espera | Se solicita la detención del executor, se restaura la interrupción y no se informa un resultado exitoso. |
| Un pedido no termina como `ENTREGADO` | `Main` informa su ID y estado, y muestra que quedaron pedidos pendientes. |

## Estructura del proyecto

```text
SpeedFastS5/
|-- src/
|   |-- app/
|   |   `-- Main.java
|   |-- model/
|   |   |-- EstadoPedido.java
|   |   |-- NivelUrgencia.java
|   |   |-- Pedido.java
|   |   `-- Repartidor.java
|   `-- sincronizado/
|       `-- ZonaDeCarga.java
|-- SpeedFastS5.iml
`-- README.md
```

## Instrucciones para clonar el repositorio

Para obtener una copia local del proyecto desde GitHub:

```bash
git clone https://github.com/Klaha0/POO-2-Carlos-Palma-Garrido.git
```

Luego entra en la carpeta de esta actividad:

```bash
cd "POO-2-Carlos-Palma-Garrido/Semana 5/SpeedFastS5"
```

## Instrucciones para compilar y ejecutar

### IntelliJ IDEA

1. Abre la carpeta `SpeedFastS5` en IntelliJ IDEA.
2. Verifica que el proyecto tenga configurado un JDK.
3. Ejecuta la clase `Main`, ubicada en el paquete `app`.

El proyecto está configurado actualmente con JDK 25.

### PowerShell

Desde la raíz de `SpeedFastS5`, ejecuta:

```powershell
$archivosJava = Get-ChildItem -Path src -Recurse -Filter *.java
javac -encoding UTF-8 -d out $archivosJava.FullName
java -cp out app.Main
```

## Simulación incluida

`Main` crea nueve pedidos destinados a distintas comunas de Santiago:

| ID | Destino | Urgencia |
|----|---------|----------|
| 1 | Santiago Centro | BAJA |
| 2 | Providencia | ALTA |
| 3 | Ñuñoa | MEDIA |
| 4 | Recoleta | ALTA |
| 5 | Las Condes | BAJA |
| 6 | Maipú | MEDIA |
| 7 | La Florida | ALTA |
| 8 | Pudahuel | BAJA |
| 9 | San Miguel | MEDIA |

Tres repartidores, Juan, Camila y Pedro, procesan estos pedidos concurrentemente. La asignación exacta de pedidos a cada persona y el orden de finalización pueden cambiar en cada ejecución.

## Salida esperada

La salida comienza mostrando los nueve pedidos agregados:

```text
[Zona de carga inicializada]
Pedido #1 agregado. Destino: Santiago Centro. Estado: PENDIENTE. Urgencia: BAJA
Pedido #2 agregado. Destino: Providencia. Estado: PENDIENTE. Urgencia: ALTA
...
```

Después se observan los retiros y entregas concurrentes:

```text
[Repartidor - Juan] Retiró el pedido #2
Destino: Providencia
Urgencia: ALTA
Estado: EN_REPARTO

[Repartidor - Camila] Entregó el pedido #4
Destino: Recoleta
Urgencia: ALTA
Estado: ENTREGADO
...
```

Al finalizar todas las tareas, el programa muestra una sola vez:

```text
Se termina la jornada, no hay más pedidos para entregar.
Todos los pedidos han sido entregados correctamente.
```

## Cómo probarlo

Puedes modificar los datos de `Main.java` para:

- Cambiar las direcciones y urgencias de los pedidos.
- Agregar más pedidos a la zona de carga.
- Crear más repartidores.
- Cambiar el tamaño del pool de hilos.
- Crear varios pedidos con la misma urgencia y comprobar el desempate por ID.
- Interrumpir la ejecución para observar cómo se recupera un pedido que estaba `EN_REPARTO`.

El orden de las entregas puede variar entre ejecuciones. Esto es parte del comportamiento esperado de un sistema concurrente.

---

© Duoc UC | Escuela de Informática y Telecomunicaciones