package dao;

import java.sql.Connection;
import java.util.ArrayList;

import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

public interface PedidoDAO {

    /**
     * Inserta un registro mediante parámetros JDBC.
     *
     * @param direccion dirección de destino validada.
     * @param tipo tipo de pedido que se almacenará.
     * @param estado estado que se almacenará.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean guardar(String direccion, TipoPedido tipo, EstadoPedido estado);
    /**
     * Consulta todos los registros de la entidad.
     *
     * @return la lista de registros; vacía si no hay resultados.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    ArrayList<Pedido> listarTodos();
    /**
     * Consulta un registro por su identificador.
     *
     * @param id identificador del registro.
     * @return el registro encontrado o null si no existe.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    Pedido buscarPorId(int id);
    /**
     * Consulta un registro por su identificador.
     *
     * @param conexion conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param id identificador del registro.
     * @return el registro encontrado o null si no existe.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    Pedido buscarPorId(Connection conexion, int id);
    /**
     * Actualiza dirección, tipo y estado del pedido.
     *
     * @param pedido pedido con los datos validados.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean actualizarPedido(Pedido pedido);
    /**
     * Actualiza dirección, tipo y estado del pedido.
     *
     * @param conexion conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param pedido pedido con los datos validados.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean actualizarPedido(Connection conexion, Pedido pedido);
    /**
     * Actualiza el estado del pedido dentro de la transacción del llamador.
     *
     * @param conexion conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param id identificador del registro.
     * @param estado estado que se almacenará.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean actualizarEstado(Connection conexion, int id, EstadoPedido estado);
    /**
     * Elimina el pedido si las relaciones de MySQL lo permiten.
     *
     * @param id identificador del registro.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean eliminarPedido(int id);
    /**
     * Pasa todos los pedidos a PENDIENTE; no elimina las entregas asociadas.
     *
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean reiniciarPedidos();
}
