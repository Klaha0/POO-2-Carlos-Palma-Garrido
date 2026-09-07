# SpeedFast - Semana 4 - Ejecutando tareas en paralelo con hilos en Java

Proyecto Java desarrollado para la asignatura **Desarrollo Orientado a Objetos II** (Duoc UC).
El sistema administra pedidos de comida, encomiendas y compras express aplicando clases abstractas, herencia, polimorfismo e interfaces, y esta semana incorpora **programación concurrente** para simular a varios repartidores entregando al mismo tiempo.

## Autor del proyecto

- **Nombre:** Carlos Palma Garrido.
- **Carrera:** Programación de aplicaciones.
- **Sede:** Online.

---

## Descripción del sistema

El sistema representa distintos tipos de pedidos mediante una jerarquía cuya clase base es `Pedido`.
Cada pedido comparte información general y posee reglas particulares para asignar repartidores y calcular su tiempo de entrega.

Sobre esa base, la entrega de esta semana agrega la clase `Repartidor`, que implementa `Runnable` y permite que cada repartidor recorra su propia ruta como un hilo independiente, ejecutándose en paralelo con los demás mediante un `ExecutorService`.

El sistema permite:

- Reservar pedidos.
- Asignar repartidores y despachar pedidos.
- Entregar pedidos y cancelar los que aún no han sido despachados.
- **Ejecutar varias rutas de reparto simultáneamente, cada una en su propio hilo.**
- Consultar el listado de pedidos cancelados y el historial de pedidos entregados.
- Guardar y recuperar los pedidos desde `resources/DatosSpeedFast.txt`.

## Principios de POO aplicados

| Principio | Dónde se aplica |
|-----------|-----------------|
| **Abstracción** | `Pedido` es una clase abstracta con datos comunes y métodos abstractos para comportamientos variables. |
| **Encapsulamiento** | Los datos identificadores se mantienen protegidos o privados y se exponen mediante operaciones controladas. |
| **Herencia** | `PedidoComida`, `PedidoExpress` y `PedidoEncomienda` extienden a `Pedido`. |
| **Sobreescritura** | Cada subclase sobrescribe `toString()`, `calcularTiempoEntrega()`, `asignarRepartidor()` y `persistir()`. |
| **Sobrecarga** | `asignarRepartidor()` posee una versión automática y otra que recibe el `Repartidor` elegido por el operador. |
| **Polimorfismo** | `Main` y `GestorDatos` trabajan con referencias de tipo `Pedido` que ejecutan el comportamiento de la subclase real. |
| **Interfaces** | `Despachable`, `Cancelable` y `Rastreable` separan responsabilidades funcionales; `Runnable` convierte al repartidor en una tarea ejecutable y `Comparable` ordena los pedidos dentro de la ruta. |

## Clases principales

| Paquete | Clase | Rol |
|---------|-------|-----|
| `model` | `Pedido` | Clase abstracta base. Administra datos comunes, estados, reserva, despacho, entrega, cancelación e historial. |
| `model` | `PedidoComida` | Pedido de comida. Calcula 15 minutos base más 2 minutos por kilómetro. |
| `model` | `PedidoExpress` | Pedido express. Calcula 10 minutos base y agrega 5 minutos si supera los 5 kilómetros. |
| `model` | `PedidoEncomienda` | Encomienda. Calcula 20 minutos base más 1,5 minutos por kilómetro. |
| `model` | `Repartidor` | Implementa `Runnable`. Mantiene su ruta de pedidos y la recorre en su propio hilo, informando el avance de cada entrega. |
| `model` | `Despachable` | Declara las operaciones `despachar()` y `entregar()`. |
| `model` | `Cancelable` | Declara la operación `cancelar()`. |
| `model` | `Rastreable` | Declara la operación `verHistorial()`. |
| `data` | `GestorDatos` | Carga, administra y reescribe todos los pedidos en el archivo de datos, y muestra los listados de cancelados y entregados. |
| `app` | `Main` | Ejecuta las pruebas de cancelación y la simulación concurrente de entregas. |

## Concurrencia

Cada `Repartidor` es una tarea `Runnable` con su propia ruta de pedidos:

- La ruta se guarda en una `PriorityBlockingQueue<Pedido>`, una cola segura entre hilos.
- `Pedido` implementa `Comparable<Pedido>` comparando por distancia, de modo que la cola entrega **primero los destinos más cercanos** aunque los pedidos se hayan asignado en otro orden.
- El método `run()` extrae los pedidos uno a uno, imprime el avance del recorrido, simula cada tramo con `Thread.sleep()` de entre 1 y 5 segundos, y marca el pedido como entregado al terminar.
- `run()` **no** está sincronizado: cada repartidor trabaja sobre su propia cola y esa cola ya es segura entre hilos, así que tomar el monitor del objeto sólo dejaría esperando a quien quisiera asignar un pedido nuevo mientras el repartidor está en ruta.
- La extracción usa `poll()` como condición del ciclo, de modo que no queda una ventana entre preguntar si hay pedidos y sacar el siguiente.
- `InterruptedException` se captura restaurando el flag de interrupción con `Thread.currentThread().interrupt()`.

En `Main`, la simulación se ejecuta así:

```java
ExecutorService jornada = Executors.newFixedThreadPool(REPARTIDORES_EN_TURNO);

for (Repartidor repartidor : repartidores) {
    jornada.execute(repartidor);
}

jornada.close();
```

`close()` apaga el executor y **bloquea hasta que el último repartidor termina su ruta**, por lo que todo lo que viene después de esa línea se ejecuta con la jornada ya completa. Gracias a eso, la comprobación de estado y el listado final de resultados siempre trabajan sobre pedidos ya entregados, sin depender de un tiempo de espera fijo.

## Reglas de cálculo implementadas

- **Pedido de comida:** 15 minutos base + 2 minutos por cada kilómetro.
- **Pedido de encomienda:** 20 minutos base + 1,5 minutos por cada kilómetro, ajustado a entero.
- **Pedido express:** 10 minutos base; si la distancia es mayor a 5 km, se agregan 5 minutos.

## Estados de un pedido

Los pedidos pueden pasar por los estados `Creado`, `Reservado`, `Despachado`, `Entregado` y `Cancelado`.
Un pedido despachado o entregado no puede ser cancelado, y sólo un pedido despachado puede entregarse.

## Manejo de errores

La aplicación está preparada para seguir funcionando ante datos dañados o incompletos:

| Situación | Respuesta del sistema |
|-----------|-----------------------|
| El archivo de datos no existe | `GestorDatos` crea la carpeta `resources` y el archivo. |
| El archivo no se puede leer o escribir | Se informa el problema y la aplicación continúa trabajando en memoria (`IOException`, `SecurityException`). |
| Un registro tiene menos campos de los esperados | Se ignora ese registro y la carga sigue con los demás. |
| La distancia o la cantidad no son numéricas | Se captura `NumberFormatException`, se ignora el registro y la carga continúa. |
| El tipo de pedido es desconocido | Se ignora el registro en lugar de dejar un hueco en la colección. |
| El archivo trae menos pedidos de los que necesita la demostración | `Main` completa los pedidos faltantes antes de usarlos. |
| Un hilo de reparto es interrumpido | `Repartidor` avisa por consola y restaura el flag de interrupción. |
| La espera de la jornada es interrumpida | `Main` corta las entregas pendientes con `shutdownNow()` y restaura el flag de interrupción. |

## Persistencia de datos

La persistencia es un agregado respecto de lo pedido en el enunciado.

`GestorDatos` crea automáticamente la carpeta `resources` y el archivo `DatosSpeedFast.txt` si no existen.
El método `persistir()` de cada subclase genera el registro correspondiente, mientras `GestorDatos` controla la escritura del archivo completo.

El flujo es siempre el mismo: al iniciar, `leerArchivo()` carga todos los pedidos en un `ArrayList<Pedido>`; durante la ejecución se trabaja sobre esa misma colección sin tocar el disco; y al terminar, `guardarTodos()` reescribe el archivo completo con `FileWriter` en modo `append = false`, de manera que los registros nunca se dupliquen.

Cada registro es una línea con cinco campos separados por punto y coma:

```plaintext
tipo;dirección;distancia;estado;detalle
PedidoComida;Los Arrayanes 568;3;Entregado;Pizza Hawaiana
PedidoComida;Los Carrera 120;4;Cancelado;Sushi 30 piezas
```

El campo `detalle` cambia según el tipo: la comida, la descripción de la encomienda o la cantidad de productos express.

## Estructura del proyecto

```plaintext
resources/
`-- DatosSpeedFast.txt
src/
|-- app/
|   `-- Main.java
|-- data/
|   `-- GestorDatos.java
`-- model/
    |-- Cancelable.java
    |-- Despachable.java
    |-- Pedido.java
    |-- PedidoComida.java
    |-- PedidoEncomienda.java
    |-- PedidoExpress.java
    |-- Rastreable.java
    `-- Repartidor.java
```

## Instrucciones para clonar el repositorio

Si deseas obtener una copia local del proyecto desde GitHub, ejecuta:

```bash
git clone https://github.com/Klaha0/POO-2-Carlos-Palma-Garrido.git
```

Luego entra a la carpeta del proyecto:

```bash
cd "Semana 4/SpeedFastSemana4"
```

## Instrucciones para compilar y ejecutar

1. Abre el proyecto en IntelliJ IDEA.
2. Ejecuta la clase `Main`, ubicada en el paquete `app`.

También puedes compilar y ejecutar desde la raíz del proyecto:

```bash
javac -encoding UTF-8 -d out src\model\*.java src\data\*.java src\app\Main.java
java -cp out app.Main
```

La simulación tarda alrededor de 15 segundos, porque cada tramo del recorrido se simula con una pausa aleatoria.

## Salida esperada

La primera ejecución crea once pedidos, con al menos tres de cada tipo, y recorre los siguientes bloques:

| Bloque | Casos que demuestra |
|--------|---------------------|
| Pruebas de cancelación | Se cancela un pedido recién creado y otro que sólo estaba reservado. |
| Preparación de la jornada | Reserva y despacho de las rutas de los tres repartidores. Cada despacho calcula el tiempo estimado y encola el pedido en su repartidor. |
| Simulación concurrente | Los tres repartidores entregan en paralelo mediante `ExecutorService`. |
| Cancelación rechazada | Terminada la jornada, se intenta cancelar un pedido ya entregado y el sistema lo impide. |
| Pedidos cancelados | Listado de los pedidos que quedaron en estado `Cancelado`. |
| Historial | Listado de los pedidos que quedaron en estado `Entregado`. |

Los pedidos se asignan a propósito de más lejano a más cercano, para comprobar que la cola de prioridad reordena la ruta y el hilo parte por el destino más próximo:

```plaintext
==== SIMULACIÓN CONCURRENTE DE ENTREGAS ====

Luis Díaz va en camino con el pedido: 10000
Marco Fuentes va en camino con el pedido: 10004
Daniela Tapia va en camino con el pedido: 10002
Marco Fuentes ya está llegando al destino del pedido: 10004
Luis Díaz ya está llegando al destino del pedido: 10000
Daniela Tapia ya está llegando al destino del pedido: 10002
Luis Díaz ha entregado el pedido: 10000
...
```

El orden exacto de las líneas cambia en cada ejecución, porque depende de las pausas aleatorias de cada hilo.

Al terminar la jornada se comprueba que un pedido ya entregado no admite cancelación y se muestran ambos listados:

```plaintext
Cancelando PedidoEncomienda 10008...
No se puede cancelar el pedido 10008 porque ya fue despachado o entregado.

Todos los repartidores terminaron sus entregas.

==== Pedidos cancelados ====
- PedidoComida 10006
- PedidoExpress 10007

==== Historial de pedidos entregados ====
- PedidoComida 10000
- PedidoEncomienda 10001
- PedidoExpress 10002
- PedidoComida 10003
- PedidoExpress 10004
- PedidoEncomienda 10005
- PedidoEncomienda 10008
```

Al terminar, la colección completa se reescribe en el archivo. En las siguientes ejecuciones esos mismos pedidos se cargan, se devuelven a estado `Creado` con `reiniciarEstados()` y vuelven a recorrer la demostración, de modo que el archivo siempre conserva la misma cantidad de registros.

## Cómo probarlo

Puedes modificar los datos creados en `Main.java` para:

- Cambiar las direcciones de entrega, las distancias y los detalles de cada pedido.
- Agregar más repartidores o cambiar la cantidad de pedidos de cada ruta.
- Ajustar `REPARTIDORES_EN_TURNO` para ver qué ocurre cuando hay menos hilos que repartidores.
- Verificar las reglas de cancelación en cada uno de los estados del pedido.

También puedes editar `resources/DatosSpeedFast.txt` con registros incompletos o mal formados para comprobar que la aplicación los descarta e igualmente continúa su ejecución.

---

© Duoc UC | Escuela de Informática y Telecomunicaciones
