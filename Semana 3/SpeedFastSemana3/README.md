# SpeedFast - Semana 3 - Actividad Sumativa 1

Proyecto Java desarrollado para la asignatura **Desarrollo Orientado a Objetos II** (Duoc UC).
El sistema administra pedidos de comida, encomiendas y compras express aplicando clases abstractas, herencia, polimorfismo e interfaces.

## Autor del proyecto

- **Nombre:** Carlos Palma Garrido.
- **Carrera:** Programación de aplicaciones.
- **Sede:** Online.

---

## Descripción del sistema

El sistema representa distintos tipos de pedidos mediante una jerarquía cuya clase base es `Pedido`.
Cada pedido comparte información general y posee reglas particulares para asignar repartidores y calcular su tiempo de entrega.

El sistema también permite:

- Reservar pedidos.
- Asignar repartidores automáticamente o mediante un nombre.
- Despachar y entregar pedidos.
- Cancelar pedidos que aún no han sido despachados.
- Consultar el historial de pedidos entregados.
- Guardar y recuperar los pedidos desde `resources/DatosSpeedFast.txt`.

## Principios de POO aplicados

| Principio | Dónde se aplica |
|-----------|-----------------|
| **Abstracción** | `Pedido` es una clase abstracta con datos comunes y métodos abstractos para comportamientos variables. |
| **Encapsulamiento** | Los datos identificadores se mantienen protegidos o privados y se exponen mediante operaciones controladas. |
| **Herencia** | `PedidoComida`, `PedidoExpress` y `PedidoEncomienda` extienden a `Pedido`. |
| **Sobreescritura** | Cada subclase sobrescribe `toString()`, `calcularTiempoEntrega()`, `asignarRepartidor()` y `persistir()`. |
| **Sobrecarga** | `asignarRepartidor()` posee una versión automática, que asigna el repartidor propio del tipo de pedido, y otra que recibe el nombre del repartidor elegido por el operador. |
| **Polimorfismo** | `Main` y `GestorDatos` trabajan con referencias de tipo `Pedido` que ejecutan el comportamiento de la subclase real. |
| **Interfaces** | `Despachable`, `Cancelable` y `Rastreable` separan responsabilidades funcionales. |

## Clases principales

| Paquete | Clase | Rol |
|---------|-------|-----|
| `model` | `Pedido` | Clase abstracta base. Administra datos comunes, estados, reserva, despacho, entrega, cancelación e historial. |
| `model` | `PedidoComida` | Pedido de comida. Calcula 15 minutos base más 2 minutos por kilómetro. |
| `model` | `PedidoExpress` | Pedido express. Calcula 10 minutos base y agrega 5 minutos si supera los 5 kilómetros. |
| `model` | `PedidoEncomienda` | Encomienda. Calcula 20 minutos base más 1,5 minutos por kilómetro. |
| `model` | `Despachable` | Declara las operaciones `despachar()` y `entregar()`. |
| `model` | `Cancelable` | Declara la operación `cancelar()`. |
| `model` | `Rastreable` | Declara la operación `verHistorial()`. |
| `data` | `GestorDatos` | Carga, administra y reescribe todos los pedidos en el archivo de datos. |
| `app` | `Main` | Ejecuta una simulación de los casos solicitados. |

## Reglas de cálculo implementadas

- **Pedido de comida:** 15 minutos base + 2 minutos por cada kilómetro.
- **Pedido de encomienda:** 20 minutos base + 1,5 minutos por cada kilómetro, ajustado a entero.
- **Pedido express:** 10 minutos base; si la distancia es mayor a 5 km, se agregan 5 minutos.

## Estados de un pedido

Los pedidos pueden pasar por los estados `Creado`, `Reservado`, `Despachado`, `Entregado` y `Cancelado`.
Un pedido despachado o entregado no puede ser cancelado.

## Persistencia de datos

La persistencia es el único agregado respecto de lo pedido en el enunciado.

`GestorDatos` crea automáticamente la carpeta `resources` y el archivo `DatosSpeedFast.txt` si no existen.
El método `persistir()` de cada subclase genera el registro correspondiente, mientras `GestorDatos` controla la escritura del archivo completo.

El flujo es siempre el mismo: al iniciar, `leerArchivo()` carga todos los pedidos en un `ArrayList<Pedido>`; durante la ejecución se trabaja sobre esa misma colección sin tocar el disco; y al terminar, `guardarTodos()` reescribe el archivo completo con `FileWriter` en modo `append = false`, de manera que los registros nunca se dupliquen.

Cada registro es una línea con campos separados por punto y coma:

```plaintext
tipo;dirección;distancia;estado;detalle;repartidor
PedidoComida;Los Arrayanes 568;3;Entregado;Pizza Hawaiana;Luis Díaz
```

El campo `detalle` cambia según el tipo: la comida, la descripción de la encomienda o la cantidad de productos express. El campo `repartidor` queda vacío cuando el pedido nunca recibió uno.

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
    `-- Rastreable.java
```

## Instrucciones para clonar el repositorio

Si deseas obtener una copia local del proyecto desde GitHub, ejecuta:

```bash
git clone https://github.com/Klaha0/POO-2-Carlos-Palma-Garrido.git
```

Luego entra a la carpeta del proyecto:

```bash
cd SpeedFastSemana3
```

## Instrucciones para compilar y ejecutar

1. Abre el proyecto en IntelliJ IDEA.
2. Ejecuta la clase `Main`, ubicada en el paquete `app`.

También puedes compilar y ejecutar desde la raíz del proyecto:

```bash
javac -encoding UTF-8 -d out src\model\*.java src\data\*.java src\app\Main.java
java -cp out app.Main
```

## Salida esperada

La primera ejecución crea once pedidos, con al menos tres de cada tipo, y recorre todos los casos del sistema:

| Bloque | Casos que demuestra |
|--------|---------------------|
| Resumen de pedidos | Recorrido polimórfico de la colección completa. |
| Reserva y asignación automática | Reserva, asignación automática, despacho, cálculo del tiempo y entrega. |
| Asignación manual y entrega | Sobrecarga de `asignarRepartidor(String)` con entrega completa. |
| Despacho sin reserva previa | Un pedido en estado `Creado` puede despacharse directamente. |
| Cancelación de pedidos | Cancelación de un pedido reservado y de uno recién creado. |
| Casos rechazados | Despacho sin repartidor, entrega sin despacho previo, reserva repetida, nombre de repartidor en blanco, despacho de un pedido cancelado, entrega repetida y cancelación de un pedido ya entregado. |
| Historial | Listado de los pedidos que quedaron en estado `Entregado`. |

```plaintext
==== RESERVA Y ASIGNACIÓN AUTOMÁTICA ====

Pedido 10000 reservado correctamente.

Buscando un repartidor para su pedido de comida...
Asignación automática: Luis Díaz para el pedido de comida.

Pedido 10000 despachado correctamente.
Tiempo estimado para comida: 21 minutos.

Pedido 10000 entregado correctamente.
```

Al terminar, la colección completa se reescribe en el archivo. En las siguientes ejecuciones esos mismos pedidos se cargan, se devuelven a estado `Creado` con `reiniciarEstados()` y vuelven a recorrer la demostración, de modo que el archivo siempre conserva la misma cantidad de registros.

## Cómo probarlo

Puedes modificar los datos creados en `Main.java` para:

- Cambiar las direcciones de entrega.
- Modificar las distancias.
- Cambiar el tipo de comida o encomienda.
- Modificar la cantidad de productos express.
- Probar asignación automática y manual.
- Verificar las reglas de cancelación y los estados del pedido.

---

© Duoc UC | Escuela de Informática y Telecomunicaciones
