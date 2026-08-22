# SpeedFast - Semana 2

Proyecto Java desarrollado para la asignatura **Desarrollo Orientado a Objetos II** (Duoc UC).
Este ejercicio modela un sistema simple de pedidos para la empresa ficticia **SpeedFast**, con el objetivo de demostrar el uso de **clases abstractas**, **herencia**, **sobreescritura** y **polimorfismo** dentro de una jerarquía de clases.

## Autor del proyecto

- **Nombre completo:** Carlos Palma Garrido.
- **Carrera:** Programación de aplicaciones.
- **Sede:** Online.

---

## Descripción del sistema

El sistema representa distintos tipos de pedidos que comparten una base común mediante la clase abstracta `Pedido`.
Esta clase define los datos generales de un pedido, como su identificador, dirección de entrega, distancia en kilómetros y tipo de pedido.

Cada clase hija hereda esa estructura común y adapta el cálculo del tiempo de entrega según las reglas indicadas para cada tipo de pedido:

- `PedidoComida`
- `PedidoExpress`
- `PedidoEncomienda`

El proyecto incluye una clase `Main` que crea instancias de los distintos pedidos, los almacena en una lista de tipo `Pedido` y ejecuta los métodos correspondientes para mostrar el resumen y el tiempo estimado de entrega.

## Principios de POO aplicados

| Principio | Dónde se aplica |
|-----------|-----------------|
| **Abstracción** | `Pedido` se define como clase abstracta porque representa un pedido genérico y no un tipo concreto de entrega. |
| **Encapsulamiento** | `idPedido`, `direccionEntrega` y `tipoPedido` se mantienen como atributos privados dentro de `Pedido`; `distanciaEnKm` queda disponible para las subclases. |
| **Herencia** | `PedidoComida`, `PedidoExpress` y `PedidoEncomienda` extienden a `Pedido` y reutilizan sus atributos y métodos comunes. |
| **Sobreescritura** | Cada subclase implementa su propia versión de `calcularTiempoEntrega()`, respetando la regla de cálculo correspondiente a cada tipo de pedido. |
| **Polimorfismo** | En `Main`, distintos objetos de clases hijas se manejan dentro de una lista `ArrayList<Pedido>`, permitiendo ejecutar el comportamiento correcto según el tipo real de cada pedido. |

## Clases principales

| Paquete | Clase | Rol |
|---------|-------|-----|
| `model` | `Pedido` | Clase abstracta base. Define los atributos comunes, genera el ID del pedido, muestra el resumen y declara el método abstracto `calcularTiempoEntrega()`. |
| `model` | `PedidoComida` | Representa pedidos de comida. Agrega el atributo propio `comida` y calcula el tiempo de entrega como 15 minutos base más 2 minutos por kilómetro. |
| `model` | `PedidoExpress` | Representa pedidos express. Agrega el atributo propio `cantidadProductos` y calcula el tiempo base de 10 minutos, sumando 5 minutos si la distancia supera los 5 km. |
| `model` | `PedidoEncomienda` | Representa pedidos de encomienda. Agrega el atributo propio `encomienda` y calcula el tiempo de entrega como 20 minutos base más 1,5 minutos por kilómetro de distancia, ajustado a entero. |
| `app` | `Main` | Punto de entrada del programa. Crea pedidos de distintos tipos, los almacena como `Pedido` y ejecuta sus métodos de forma polimórfica. |

## Reglas de cálculo implementadas

- **Pedido de comida:** 15 minutos base + 2 minutos por cada kilómetro.
- **Pedido de encomienda:** 20 minutos base + 1,5 minutos por cada kilómetro, ajustado a entero.
- **Pedido express:** 10 minutos base; si la distancia es mayor a 5 km, se agregan 5 minutos.

## Estructura del proyecto

```plaintext
src/
|-- app/
|   `-- Main.java
`-- model/
    |-- Pedido.java
    |-- PedidoComida.java
    |-- PedidoEncomienda.java
    `-- PedidoExpress.java
```

## Instrucciones para clonar el repositorio

Si deseas obtener una copia local del proyecto desde GitHub, ejecuta:

```bash
git clone https://github.com/Klaha0/POO-2-Carlos-Palma-Garrido.git
```

Luego entra a la carpeta del proyecto:

```bash
cd POO-2-Carlos-Palma-Garrido
```

## Instrucciones para compilar y ejecutar

1. Abre el proyecto en tu IDE Java de preferencia, como IntelliJ IDEA o NetBeans.
2. Compila las clases del proyecto.
3. Ejecuta la clase principal `Main`, ubicada en el paquete `app`.

Si quieres probarlo por consola desde la raíz del proyecto:

```bash
javac -d out src\model\*.java src\app\Main.java
java -cp out app.Main
```

## Salida esperada

Al ejecutar el programa se muestra por consola el resumen de cada pedido y su tiempo estimado de entrega.
La salida incluye ejemplos de:

- un pedido de comida,
- dos pedidos express con distintas distancias,
- una encomienda.

Cada resultado evidencia que el método `calcularTiempoEntrega()` se comporta de forma distinta según la subclase que lo implementa.

## Cómo probarlo

Puedes modificar los datos de ejemplo en `src/app/Main.java` para:

- cambiar las direcciones de entrega,
- modificar la distancia en kilómetros,
- cambiar el nombre de la comida solicitada,
- cambiar la cantidad de productos del pedido express,
- cambiar el tipo de encomienda,
- probar distancias menores, iguales o mayores a 5 km en pedidos express.

---

**Enfoque académico del ejercicio:** demostrar de forma práctica el uso de una clase abstracta como base común y el polimorfismo mediante métodos sobrescritos en clases hijas.

---

© Duoc UC | Escuela de Informática y Telecomunicaciones
