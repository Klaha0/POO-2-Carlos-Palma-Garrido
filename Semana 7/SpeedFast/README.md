# 🚚 SpeedFast — Semana 7

Aplicación de escritorio para la gestión de **pedidos, repartidores y entregas** de SpeedFast.
El sistema permite administrar los registros desde una interfaz gráfica **Java Swing**, con
persistencia en **MySQL** mediante **JDBC** y organización del proyecto con **Maven**.

Proyecto desarrollado para la asignatura **Desarrollo Orientado a Objetos II** (Duoc UC).

## 👤 Autor del proyecto

* **Nombre completo:** Carlos Palma Garrido.
* **Carrera:** Programación de aplicaciones.
* **Sede:** Online.

---

## 📄 Descripción del sistema

SpeedFast permite **registrar, consultar, buscar, actualizar y eliminar** pedidos,
repartidores y entregas. La aplicación organiza sus responsabilidades en modelos,
vistas, controladores y clases de acceso a datos (DAO).

Características principales:

* **Gestión de pedidos:** registro de dirección y tipo de pedido (Express, Comida o
  Encomienda), con estado inicial `PENDIENTE` e ID generado por MySQL.
* **Nivel de urgencia:** se obtiene automáticamente a partir del tipo de pedido.
* **Gestión de repartidores:** registro y administración de repartidores mediante ID y nombre.
* **Gestión de entregas:** asociación de un pedido con un repartidor, con fecha y hora
  asignadas por la base de datos.
* **Edición en tablas:** actualización de los campos editables directamente desde el listado.
* **Búsqueda y ordenamiento:** filtro por ID y ordenamiento al pulsar los encabezados.
* **Confirmaciones y errores:** confirmación antes de eliminar y mensajes ante fallos de
  validación o de acceso a la base de datos.

## 🧠 Diseño orientado a objetos

| Concepto | Dónde se aplica |
|----------|-----------------|
| **Encapsulamiento** | `Pedido` protege sus atributos y valida dirección, tipo y estado. |
| **Herencia y abstracción** | `VentanaCrud` es una clase abstracta que extiende `JFrame` y reúne el comportamiento común de las ventanas de gestión. |
| **Reutilización** | Las ventanas CRUD comparten edición, búsqueda, eliminación y manejo de errores. |
| **Enumeraciones** | `TipoPedido`, `EstadoPedido` y `NivelUrgencia` representan los valores del dominio. |
| **Separación de responsabilidades** | Las vistas presentan los datos, los controladores validan y los DAO ejecutan las operaciones SQL. |

## 🧩 Clases principales

| Paquete | Elemento | Rol |
|---------|----------|-----|
| `app` | `Main` | Inicia la interfaz Swing mediante `invokeLater`. |
| `modelo` | `Pedido`, `Repartidor`, `Entrega` | Representan las entidades del sistema. |
| `modelo` | `TipoPedido`, `EstadoPedido`, `NivelUrgencia` | Definen tipos, estados y urgencias de los pedidos. |
| `dao` | `ConexionBD` | Configura la conexión JDBC con MySQL. |
| `dao` | `PedidoDAO`, `RepartidorDAO`, `EntregaDAO` | Consultan, insertan, actualizan y eliminan registros. |
| `controlador` | `PedidoControlador`, `RepartidorControlador`, `EntregaControlador` | Validan las operaciones y acceden a los DAO. |
| `vista` | `VentanaPrincipal` | Presenta el menú principal. |
| `vista` | `VentanaRegistroPedido` | Permite registrar un pedido. |
| `vista` | `VentanaCrud` | Comparte el comportamiento de los listados editables. |
| `vista` | `VentanaListaPedidos`, `VentanaRepartidores`, `VentanaEntregas` | Administran los registros de cada entidad. |
| `vista` | `SelectorRegistro` | Representa las opciones de selección de registros. |

## 🧱 Estructura del proyecto

```plaintext
📁 src/main/java/
├── app/           # Punto de entrada: Main.
├── modelo/        # Entidades: Pedido, Repartidor, Entrega.
│                  # Enumeraciones: TipoPedido, EstadoPedido, NivelUrgencia.
├── dao/           # Conexión y persistencia: ConexionBD, PedidoDAO,
│                  # RepartidorDAO, EntregaDAO.
├── controlador/   # Validación y operaciones: PedidoControlador,
│                  # RepartidorControlador, EntregaControlador.
└── vista/         # Interfaz Swing: VentanaPrincipal, VentanaRegistroPedido,
                   # VentanaCrud, VentanaListaPedidos, VentanaRepartidores,
                   # VentanaEntregas, SelectorRegistro.

📁 docs/formularios/
├── README.md                    # Descripción de los diseños originales.
├── VentanaListaPedidos.form     # Formularios históricos de referencia.
├── VentanaPrincipal.form
└── VentanaRegistroPedido.form

📁 .mvn/wrapper/
└── maven-wrapper.properties     # Configuración de Maven Wrapper.

📁 SpeedFast/                    # Archivos en la raíz del proyecto.
├── pom.xml                      # Dependencias y configuración de Maven.
├── mvnw                         # Maven Wrapper para Linux/macOS.
├── mvnw.cmd                     # Maven Wrapper para Windows.
├── compilar.ps1                 # Script de compilación y ejecución.
├── SpeedFast.iml                # Configuración del módulo de IntelliJ.
├── .gitignore                   # Exclusiones de Git.
└── README.md                    # Documentación del proyecto.
```

Los archivos de `docs/formularios` son diseños históricos de referencia y no participan
en la compilación. La interfaz activa se construye en las clases Java del paquete `vista`.

## ⚙️ Instrucciones para ejecutar el proyecto

1. Instala **JDK 17 o superior** y dispone de un servidor **MySQL**.
2. Prepara la base `speedfast_db` con las tablas de la pauta y revisa los parámetros de
   conexión en `src/main/java/dao/ConexionBD.java` cambiando los datos de USUARIO y CLAVE por los tuyos.
3. Abre una terminal PowerShell en la carpeta del proyecto y ejecuta:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd exec:java
```

Maven Wrapper descarga Maven y las dependencias si aún no están disponibles.
El comando `exec:java` incluye el conector JDBC en el classpath.

También puedes abrir `pom.xml` en **IntelliJ IDEA**, recargar Maven y ejecutar la clase
**`app.Main`**.

Como alternativa, utiliza el script de PowerShell:

```powershell
.\compilar.ps1
.\compilar.ps1 -Ejecutar
```

## 🖥️ Uso de la aplicación

1. **Registrar Pedido:** ingresa una dirección y selecciona el tipo de pedido.
2. **Gestionar Pedidos:** consulta, edita o elimina pedidos. Desde esta ventana también
   puedes abrir el formulario de registro.
3. **Gestionar Repartidores:** ingresa un nombre para registrar un repartidor y administra
   los existentes en la tabla.
4. **Gestionar Entregas:** selecciona un pedido y un repartidor para registrar una entrega.
   Se requiere al menos un registro de cada uno. **Actualizar opciones** carga los registros
   creados desde otras ventanas.

### ✏️ Edición y consulta de registros

Haz doble clic en una celda editable. Al pulsar **Enter** o salir de ella se guarda el
cambio mediante el controlador y su DAO. **Esc** cancela la edición de esa celda.
Si el guardado falla, se conserva el valor anterior y se muestra un mensaje.

| Tabla | Campos editables | Campos automáticos, de solo lectura |
|-------|------------------|--------------------------------------|
| **Pedidos** | Dirección, tipo y estado | ID y urgencia (depende del tipo) |
| **Repartidores** | Nombre | ID |
| **Entregas** | Pedido y repartidor, mediante listas | ID, fecha y hora |

* **Buscar ID:** filtra los registros del listado cargado.
* **Ver todos:** quita el filtro.
* **Refrescar:** consulta nuevamente la base de datos.
* **Encabezados:** permiten ordenar los registros de la tabla.
* **Eliminar seleccionado:** solicita confirmación antes de eliminar el registro.

Si un pedido o repartidor tiene entregas asociadas, la base de datos puede impedir su
eliminación: elimina primero las entregas correspondientes. Si una consulta falla, se
conserva el último listado visible y se muestra un aviso.

## 💾 Base de datos y persistencia

> **📌 Recordatorio para el examinador (profesor):** antes de ejecutar la aplicación,
> cambie las credenciales de acceso a MySQL en `src/main/java/dao/ConexionBD.java`.
> Actualmente están configuradas las credenciales del autor del proyecto. Reemplace
> el usuario y la contraseña por los de su entorno y ajuste la URL de conexión si corresponde.

La aplicación utiliza la base **`speedfast_db`** y las tablas **`pedido`**, **`repartidor`**
y **`entrega`**, con las relaciones definidas en la pauta.

El flujo de pedidos utiliza las columnas `id`, `direccion`, `tipo` y `estado` de la tabla
`pedido`. El campo `id` debe ser autoincremental. El tipo y el estado se almacenan con los
nombres de las enumeraciones Java.

**Fecha y hora de entrega:** MySQL las asigna con `DATE(NOW())` y `TIME(NOW())` tanto al
insertar como al actualizar una entrega. La tabla vuelve a consultar estos valores
después del cambio.

**Estado del pedido:** al registrar una entrega, el pedido asociado pasa automáticamente
a `ENTREGADO`. La entrega y el cambio de estado se guardan juntos: si falla alguna
operación, se deshacen ambos cambios. Si el listado de pedidos está abierto, se actualiza
para mostrar el nuevo estado.

**Validaciones:** `EntregaControlador` comprueba que el pedido y el repartidor existan,
que el pedido no esté entregado y que no tenga otra entrega registrada. Al editar una
entrega, permite conservar su pedido actual, pero no asociarla a otro ya entregado.
`PedidoControlador` impide volver a pendiente o en reparto un pedido entregado o con
una entrega registrada. Eliminar una entrega no vuelve a habilitar ese pedido.

Las reglas de negocio están en los controladores. Los DAO realizan las consultas y
escrituras SQL; los controladores coordinan la transacción para que la entrega y el
estado del pedido se guarden juntos. La pantalla pide seleccionar explícitamente
un pedido disponible y no selecciona el siguiente automáticamente después de guardar.

Esta versión se centra en la gestión de registros con MySQL. Se eliminaron la simulación
de entregas, la zona de carga, los hilos de repartidores y Reset. `Repartidor` contiene
únicamente ID y nombre.

---

**Cómo probarlo:** ejecuta `app.Main` con MySQL disponible, registra un pedido y un
repartidor, y luego asócialos desde **Gestionar Entregas**. Consulta los listados y edita
una celda para comprobar que los cambios se guardan.

---

© Duoc UC | Escuela de Informática y Telecomunicaciones | Semana 7
