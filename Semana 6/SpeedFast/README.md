# SpeedFast - Semana 6 - Diseñando interfaces gráficas para aplicaciones en Java

Proyecto Java desarrollado para la asignatura **Desarrollo Orientado a Objetos II** de Duoc UC.

La aplicación permite gestionar pedidos de SpeedFast mediante una interfaz gráfica construida con Java Swing. Desde sus ventanas se pueden registrar pedidos, consultar su información y comenzar una simulación de entregas con tres repartidores concurrentes.

El proyecto aplica el patrón **Modelo-Vista-Controlador (MVC)** y conserva los formularios `.form` del diseñador visual de IntelliJ IDEA. La urgencia se asigna automáticamente según el tipo de pedido y las entregas se ejecutan sin bloquear la interfaz.

## Autor del proyecto

- **Nombre:** Carlos Palma Garrido.
- **Carrera:** Programación de aplicaciones.
- **Sede:** Online.

---

## Descripción del sistema

El usuario registra pedidos indicando una dirección y un tipo: Express, Comida o Encomienda. El sistema genera un identificador único y determina la urgencia correspondiente.

Al presionar **Asignar repartidor / Iniciar entrega**, los pedidos pendientes se agregan a una zona de carga compartida. Tres repartidores los retiran por prioridad y simulan sus entregas. La tabla muestra los cambios de estado y el repartidor asignado a cada pedido.

La aplicación permite:

- Registrar pedidos con validación de dirección y tipo.
- Asignar automáticamente el ID y la urgencia.
- Consultar los pedidos en una `JTable` con `DefaultTableModel`.
- Actualizar el listado automáticamente o mediante el botón **Refrescar**.
- Navegar entre la ventana principal, el formulario y el listado.
- Ejecutar tres repartidores simultáneamente sin bloquear la interfaz.
- Simular entregas con `Thread.sleep()` durante un tiempo aleatorio de 1 a 6 segundos.
- Registrar nuevos pedidos mientras se procesa un lote de entregas.
- Usar **Reset** para devolver los pedidos a su estado inicial y repetir el reparto.

Los datos se almacenan en una lista en memoria compartida mediante el controlador. No se utiliza una base de datos y los registros se pierden al cerrar la aplicación.

## Principios de POO aplicados

| Principio | Dónde se aplica |
|-----------|-----------------|
| **Encapsulamiento** | Los atributos del modelo son privados y se consultan mediante sus métodos. El controlador administra la lista y devuelve una copia no modificable de ella. |
| **Abstracción** | Cada clase representa un elemento del problema: pedido, repartidor, zona de carga o ventana. |
| **Responsabilidad única** | El modelo representa los datos y las entregas, el controlador coordina las operaciones y las vistas presentan la información. |
| **Herencia** | `VentanaPrincipal`, `VentanaRegistroPedido` y `VentanaListaPedidos` heredan de `JFrame`. |
| **Composición** | Cada repartidor recibe la misma zona de carga y las ventanas comparten un controlador. |
| **Interfaces** | `Repartidor` implementa `Runnable` para ejecutar su tarea en un hilo independiente. |
| **Enumeraciones** | `EstadoPedido`, `NivelUrgencia` y `TipoPedido` restringen los valores posibles. `TipoPedido` establece la urgencia asociada. |

## Clases principales

| Paquete | Clase | Rol |
|---------|-------|-----|
| `modelo` | `Pedido` | Mantiene el ID, la dirección, el tipo, el estado y el nombre del repartidor. Obtiene la urgencia desde el tipo de pedido. |
| `modelo` | `EstadoPedido` | Define los estados `PENDIENTE`, `EN_REPARTO` y `ENTREGADO`. |
| `modelo` | `NivelUrgencia` | Define las urgencias `BAJA`, `MEDIA` y `ALTA`, asociadas a los valores 1, 2 y 3. |
| `modelo` | `TipoPedido` | Define los tipos Express, Comida y Encomienda, junto con su urgencia automática. |
| `modelo` | `Repartidor` | Implementa `Runnable`, retira pedidos, simula el traslado y registra su entrega. |
| `sincronizado` | `ZonaDeCarga` | Administra la cola compartida y ordena los pedidos por urgencia e ID. |
| `controlador` | `PedidoControlador` | Administra los registros, coordina la simulación y notifica cambios a las vistas. |
| `vista` | `VentanaPrincipal` | Presenta las acciones de registro, listado, inicio de entregas y salida. |
| `vista` | `VentanaRegistroPedido` | Captura los datos, muestra la urgencia automática y presenta avisos de validación y confirmación. |
| `vista` | `VentanaListaPedidos` | Muestra los pedidos y sus estados en una tabla actualizada. |
| `main` | `Main` | Inicia la ventana principal en el hilo de eventos de Swing. |

## Organización MVC

El **modelo** contiene los datos y el comportamiento de los pedidos y repartidores, sin depender de las ventanas. La zona de carga administra el recurso compartido de la simulación.

Las **vistas** capturan las acciones del usuario y muestran formularios, tablas y mensajes. Cada ventana está vinculada a su archivo `.form`, que permite editar su diseño desde IntelliJ IDEA.

El **controlador** conserva la lista común, recibe las solicitudes de registro e inicio de entregas y avisa a las ventanas cuando cambian los datos. De esta manera, el registro y el listado trabajan con los mismos pedidos.

## Concurrencia y sincronización

Los tres repartidores utilizan la misma instancia de `ZonaDeCarga` durante cada lote. Esta clase almacena los pedidos en una `PriorityBlockingQueue<Pedido>`, preparada para el acceso concurrente.

Los métodos de ingreso y retiro están sincronizados:

```java
public synchronized void agregarPedido(Pedido pedido)
public synchronized Pedido retirarPedido()
```

`retirarPedido()` utiliza `poll()`, que extrae un solo elemento. Una vez retirado, el pedido deja de estar disponible para los demás repartidores, evitando retiros duplicados.

En `Repartidor`, el retiro, la asignación del nombre y el cambio a `EN_REPARTO` ocurren dentro del bloque sincronizado. La notificación y la espera del viaje ocurren fuera del bloqueo, permitiendo que los demás repartidores continúen trabajando.

El estado y el nombre del repartidor utilizan `volatile` para que sus cambios sean visibles entre hilos. La lista del controlador y las actualizaciones de la tabla se manejan desde el hilo de eventos de Swing.

## Orden de prioridad

La urgencia depende del tipo de pedido y no puede editarse desde el formulario:

| Tipo de pedido | Urgencia | Prioridad |
|----------------|----------|-----------|
| Express | ALTA | 3 |
| Comida | MEDIA | 2 |
| Encomienda | BAJA | 1 |

`ZonaDeCarga` utiliza un `Comparator<Pedido>` con dos reglas:

1. Se retira primero el pedido de mayor urgencia.
2. A igual urgencia, se retira primero el pedido de menor ID.

El orden general de despacho es:

```text
EXPRESS (ALTA) -> COMIDA (MEDIA) -> ENCOMIENDA (BAJA)
```

El orden de finalización puede variar porque los viajes ocurren en paralelo. Cada repartidor espera un número entero aleatorio de segundos entre 1 y 6, ambos incluidos:

```java
Thread.sleep((random.nextInt(6) + 1) * 1000L);
```

## Identificadores seguros

`Pedido` utiliza un `AtomicInteger` estático. La operación `incrementAndGet()` genera cada ID de forma atómica y el resultado se conserva en un atributo `final int`.

El formulario muestra que el ID se asignará al guardar y no permite editarlo. La confirmación informa el número generado. Los identificadores son únicos durante la ejecución; el contador comienza nuevamente al reiniciar la aplicación.

## Estados de un pedido

El ciclo normal es:

```text
PENDIENTE -> EN_REPARTO -> ENTREGADO
```

| Estado | Significado |
|--------|-------------|
| `PENDIENTE` | El pedido está registrado y espera su retiro en el lote actual o su inclusión en el siguiente. |
| `EN_REPARTO` | Un repartidor retiró el pedido y está simulando su entrega. |
| `ENTREGADO` | El repartidor completó la entrega. |

Si un repartidor es interrumpido durante la espera, el pedido vuelve a `PENDIENTE`, queda sin repartidor asignado y regresa a la zona de carga antes de que el hilo termine.

## Administración de la simulación asíncrona

`PedidoControlador` crea tres objetos `Thread`, uno para cada repartidor, y los inicia mediante `start()`. Las entregas y las esperas con `Thread.sleep()` ocurren en esos hilos, dejando disponible el hilo de eventos de Swing (EDT).

Los cambios se comunican mediante `SwingUtilities.invokeLater()`, que permite actualizar los componentes desde el hilo de eventos de Swing. Cada repartidor avisa al terminar y el controlador reduce un contador de repartidores activos. Cuando llega a cero, informa el resultado y vuelve a habilitar el botón de inicio.

Mientras la simulación está activa, el usuario puede seguir utilizando las ventanas y registrar pedidos. El lote contiene los pedidos pendientes al pulsar el botón; los nuevos registros esperan al siguiente inicio. Los pedidos entregados no se vuelven a enviar hasta que se utilice **Reset**.

El botón **Reset**, ubicado en el listado, devuelve todos los pedidos a `PENDIENTE` y elimina su asignación de repartidor. Conserva los ID, direcciones, tipos y urgencias. La tabla se actualiza inmediatamente y se puede iniciar nuevamente el reparto desde la ventana principal. Reset está deshabilitado durante las entregas y cuando no hay pedidos.

Cerrar la ventana principal interrumpe los hilos de los repartidores. Cerrar solamente el formulario o el listado permite volver a abrirlos sin perder los registros de la sesión.

## Manejo de errores

| Situación | Respuesta del sistema |
|-----------|-----------------------|
| La dirección está vacía o contiene solo espacios | Se rechaza el registro y se muestra un aviso. |
| No se selecciona un tipo de pedido | Se solicita seleccionar un tipo antes de guardar. |
| Se intenta iniciar entregas sin pedidos pendientes | Se muestra un mensaje informativo. |
| Ya existe una simulación activa | El botón permanece deshabilitado y el controlador evita iniciar otro lote simultáneo. |
| Se intenta usar Reset mientras hay entregas | El botón está deshabilitado y el controlador rechaza el reinicio hasta que terminen los repartidores. |
| Se crea una zona de carga con capacidad inicial igual o menor que cero | Se lanza `IllegalArgumentException`. |
| La zona de carga queda vacía | `retirarPedido()` devuelve `null` y el repartidor termina su tarea. |
| Un repartidor es interrumpido durante el viaje | Devuelve el pedido a pendiente y a la cola, y restaura el indicador de interrupción. |

## Estructura del proyecto

```text
SpeedFast/
|-- src/
|   |-- controlador/
|   |   `-- PedidoControlador.java
|   |-- main/
|   |   `-- Main.java
|   |-- modelo/
|   |   |-- EstadoPedido.java
|   |   |-- NivelUrgencia.java
|   |   |-- Pedido.java
|   |   |-- Repartidor.java
|   |   `-- TipoPedido.java
|   |-- sincronizado/
|   |   `-- ZonaDeCarga.java
|   `-- vista/
|       |-- VentanaPrincipal.java
|       |-- VentanaPrincipal.form
|       |-- VentanaRegistroPedido.java
|       |-- VentanaRegistroPedido.form
|       |-- VentanaListaPedidos.java
|       `-- VentanaListaPedidos.form
|-- tests/
|   `-- PruebasSpeedFast.java
|-- SpeedFast.iml
|-- build.xml
|-- compilar.ps1
`-- README.md
```

## Instrucciones para clonar el repositorio

El repositorio configurado actualmente para los trabajos de la asignatura es:

```bash
git clone https://github.com/Klaha0/POO-2-Carlos-Palma-Garrido.git
```

Una vez publicada esta actividad en ese repositorio, entra en su carpeta:

```bash
cd "POO-2-Carlos-Palma-Garrido/Semana 6/SpeedFast"
```

Estos comandos corresponden al repositorio configurado localmente; no confirman que los cambios de esta semana ya estén publicados. Si se utiliza un repositorio independiente para la entrega, se debe reemplazar la URL y la ruta por las correspondientes.

## Instrucciones para compilar y ejecutar

### IntelliJ IDEA

1. Abre la carpeta `SpeedFast` como proyecto.
2. Selecciona un JDK 17 o superior en **File > Project Structure > Project SDK**.
3. Mantén `src` como carpeta de código fuente y utiliza el compilador de IntelliJ con la generación de formularios en **Binary class files**, dentro de la configuración de GUI Designer.
4. Ejecuta la clase `Main`, ubicada en el paquete `main`.

El proyecto está configurado con Temurin JDK 25. Si no está instalado, selecciona un JDK compatible disponible.

Los archivos `.form` deben procesarse durante la construcción. Compilar solamente los `.java` con `javac` no inicializa los componentes del diseñador visual.

### PowerShell

Desde la raíz de `SpeedFast`, ejecuta:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\compilar.ps1 -Ejecutar
```

Para compilar sin abrir la aplicación:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\compilar.ps1
```

El script requiere `java` en PATH y una instalación de IntelliJ IDEA con los componentes Java y Gradle/Ant. Utiliza el compilador de formularios de IDEA y genera las clases en `out/`.

Detecta IntelliJ en `%LOCALAPPDATA%\Programs`. Para otra ubicación, agrega `-IdeaHome 'C:\ruta\a\IntelliJ IDEA'` o define la variable `IDEA_HOME`. La opción `ExecutionPolicy Bypass` se aplica solo al proceso del comando, sin cambiar la configuración permanente del sistema.

## Simulación desde la interfaz

La aplicación inicia sin pedidos precargados. Para demostrar su funcionamiento:

1. Presiona **Registrar Pedido** y guarda varios registros con distintos tipos.
2. Comprueba que la urgencia se muestra automáticamente al seleccionar el tipo.
3. Abre **Listar Pedidos** y revisa los datos registrados.
4. Presiona **Asignar repartidor / Iniciar entrega** desde la ventana principal.
5. Observa los cambios en el listado mientras continúas utilizando la interfaz.
6. Registra otro pedido durante la simulación y comprueba que queda pendiente para el siguiente lote.
7. Cuando terminen las entregas, presiona **Reset** en el listado. Comprueba que todos quedan pendientes y sin asignar, e inicia nuevamente el reparto desde la ventana principal.

Puedes utilizar estos destinos de ejemplo:

| Destino | Tipo | Urgencia automática |
|---------|------|---------------------|
| Santiago Centro | Encomienda | BAJA |
| Providencia | Express | ALTA |
| Ñuñoa | Comida | MEDIA |
| Recoleta | Express | ALTA |
| Las Condes | Encomienda | BAJA |
| Maipú | Comida | MEDIA |

Los trabajadores se identifican como `Repartidor 1`, `Repartidor 2` y `Repartidor 3`. La asignación exacta y el orden de finalización pueden cambiar en cada ejecución.

## Salida esperada

El listado presenta las columnas **ID, Dirección, Tipo, Urgencia, Estado y Repartidor**. La tabla se actualiza al registrar pedidos y cuando cambian sus estados. También muestra los totales de pedidos pendientes, en reparto y entregados.

La consola registra eventos como los siguientes; los ID y repartidores dependen de la ejecución:

```text
Pedido #2 agregado. Destino: Providencia. Estado: PENDIENTE. Urgencia: ALTA
[Repartidor 1] Pedido #2: EN_REPARTO
[Repartidor 1] Pedido #2: ENTREGADO
```

Al finalizar normalmente el lote, la ventana principal muestra:

```text
Entregas finalizadas.
```

El botón de inicio vuelve a habilitarse. Si se registraron pedidos durante la simulación, estos permanecen pendientes hasta comenzar un nuevo lote.

## Cómo probarlo

Desde la interfaz puedes:

- Intentar guardar una dirección vacía o un pedido sin tipo.
- Cambiar el tipo y comprobar la urgencia automática.
- Registrar varios Express y observar el desempate por ID.
- Revisar que el listado se actualice sin cerrarlo.
- Registrar pedidos durante las entregas para comprobar que la interfaz responde.
- Iniciar otro lote y verificar que los pedidos entregados no se repitan.
- Cerrar y volver a abrir las ventanas de registro y listado.
- Usar **Reset** al finalizar y repetir el reparto con los mismos pedidos.

Para ejecutar las pruebas automatizadas:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\compilar.ps1 -Probar
```

`PruebasSpeedFast` comprueba la urgencia automática, el orden de prioridad, las validaciones, los formularios, la actualización de la tabla, las entregas concurrentes, la respuesta del EDT, el segundo lote y la devolución de un pedido interrumpido. También verifica que Reset restaure los estados y las asignaciones, conserve los pedidos y permita repetir el reparto. Las pruebas construyen ventanas sin mostrarlas y requieren un entorno de escritorio.

El orden de finalización puede variar entre ejecuciones. Esto es parte del comportamiento esperado de un sistema concurrente.

---

© Duoc UC | Escuela de Informática y Telecomunicaciones
