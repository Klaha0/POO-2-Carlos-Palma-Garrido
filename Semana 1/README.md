# SpeedFast

Proyecto Java desarrollado para la asignatura **Desarrollo Orientado a Objetos II** (Duoc UC).  
Este ejercicio modela un sistema simple de pedidos para la empresa ficticia **SpeedFast**, con el objetivo de demostrar el uso de **herencia**, **sobreescritura** y **sobrecarga** dentro de una jerarquía de clases.

## Autor del proyecto

- **Nombre completo:** Carlos Palma Garrido.
- **Carrera:** Programacion de aplicaciones.
- **Sede:** Online.

---

## Descripcion del sistema

El sistema representa distintos tipos de pedidos que comparten una base comun (`Pedido`) y especializan su comportamiento en subclases concretas:

- `PedidoComida`
- `PedidoExpress`
- `PedidoEncomienda`

Cada pedido recibe una direccion de entrega y un identificador unico autogenerado. Luego, cada subtipo redefine la forma en que se asigna un repartidor, agregando reglas y mensajes propios segun su contexto.

El proyecto incluye una clase `Main` que crea ejemplos de los tres tipos de pedido y muestra por consola el resultado de asignarles un repartidor.

## Principios de POO aplicados

| Principio | Donde se aplica |
|-----------|-----------------|
| **Encapsulamiento** | La clase `Pedido` mantiene `idPedido` como atributo `private`, y las subclases gestionan sus propios atributos mediante metodos de acceso. |
| **Herencia** | `PedidoComida`, `PedidoExpress` y `PedidoEncomienda` extienden a `Pedido`. |
| **Sobreescritura** | Cada subclase redefine `AsignarRepartidor()` para adaptar el comportamiento base al tipo de pedido. |
| **Sobrecarga** | Además del metodo base `AsignarRepartidor()`, las subclases implementan `AsignarRepartidor(String nombreRepartidor)` para asignar un repartidor especifico. |
| **Polimorfismo** | Las subclases comparten una estructura comun y reutilizan el método de la superclase mediante `super.AsignarRepartidor()`. |

## Clases principales

| Paquete | Clase | Rol |
|---------|-------|-----|
| `model` | `Pedido` | Superclase del sistema. Gestiona la dirección de entrega, el ID unico y el comportamiento base de asignación de repartidor. IMPORTANTE. Se reemplaza el atributo "tipoPedido" por lógicas un poco más complejas en las clases hijas para brindar un mejor manéjo personalizado en cada una de estas.|
| `model` | `PedidoComida` | Representa pedidos de comida y valida condiciones asociadas al transporte, como la mochila térmica. |
| `model` | `PedidoExpress` | Modela pedidos express con verificación de repartidor cercano disponible y una lista de productos. |
| `model` | `PedidoEncomienda` | Representa encomiendas y valida condiciones de peso y embalaje antes de asignar al repartidor. |
| `app` | `Main` | Punto de entrada del programa. Crea ejemplos y muestra por consola el comportamiento de cada pedido. |

## Comportamiento de cada tipo de pedido

- **Pedido de comida:** agrega una verificacion de mochila termica antes de completar la asignación.
- **Pedido express:** valida si existe un repartidor cercano disponible y detalla la lista de productos asociados al pedido.
- **Pedido de encomienda:** valida peso y embalaje antes de aceptar la asignación del repartidor.

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
3. Ejecuta la clase principal `Main`.

Si quieres probarlo por consola desde la raiz del proyecto:

```bash
javac -d out src\model\*.java src\app\Main.java
java -cp out Main
```

## Salida esperada

Al ejecutar el programa se muestra en consola la asignacion de repartidores para:

- un pedido de comida,
- un pedido express,
- una encomienda.

Cada salida evidencia como cada subclase reutiliza y extiende el comportamiento definido en `Pedido`.

## Como probarlo

Puedes modificar los datos de ejemplo en `src/app/Main.java` para:

- cambiar las direcciones de entrega,
- asignar distintos nombres de repartidores,
- agregar o quitar productos de un pedido express,
- probar casos donde no haya repartidor cercano disponible,
- probar encomiendas que no cumplan la validación de embalaje y peso.

---

**Enfoque academico del ejercicio:** demostrar de forma práctica la diferencia entre **sobreescritura** y **sobrecarga** en clases derivadas.

---

© Duoc UC | Escuela de Informatica y Telecomunicaciones
