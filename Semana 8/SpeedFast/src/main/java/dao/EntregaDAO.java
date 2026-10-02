package dao;

import java.sql.Connection;
import java.util.ArrayList;

import modelo.Entrega;

public interface EntregaDAO {
    /**
     * Inserta un registro mediante parámetros JDBC.
     *
     * @param idPedido identificador del pedido asociado.
     * @param idRepartidor identificador del repartidor asociado.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean guardar(int idPedido, int idRepartidor);
    /**
     * Inserta un registro mediante parámetros JDBC.
     *
     * @param conn conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param idPedido identificador del pedido asociado.
     * @param idRepartidor identificador del repartidor asociado.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean guardar(Connection conn, int idPedido, int idRepartidor);
    /**
     * Comprueba si el pedido tiene otra entrega asociada.
     *
     * @param conn conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param idPedido identificador del pedido asociado.
     * @param idEntregaExcluida entrega que se excluye de la búsqueda; 0 para no excluir ninguna.
     * @return true si se encontró una entrega distinta de la excluida.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean existeParaPedido(Connection conn, int idPedido, int idEntregaExcluida);
    /**
     * Consulta todas las entregas registradas.
     *
     * @return la lista de registros; vacía si no hay resultados.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    ArrayList<Entrega> listarTodas();
    /**
     * Consulta un registro por su identificador.
     *
     * @param id identificador del registro.
     * @return el registro encontrado o null si no existe.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    Entrega buscarPorId(int id);
    /**
     * Consulta un registro por su identificador.
     *
     * @param conn conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param id identificador del registro.
     * @return el registro encontrado o null si no existe.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    Entrega buscarPorId(Connection conn, int id);
    /**
     * Actualiza únicamente el repartidor de la entrega.
     *
     * @param entrega entrega con el nuevo repartidor; se conservan pedido, fecha y hora.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean actualizarEntrega(Entrega entrega);
    /**
     * Actualiza únicamente el repartidor de la entrega.
     *
     * @param conn conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param entrega entrega con el nuevo repartidor; se conservan pedido, fecha y hora.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean actualizarEntrega(Connection conn, Entrega entrega);
    /**
     * Elimina la entrega; el controlador coordina el reinicio del pedido asociado.
     *
     * @param id identificador del registro.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean eliminarEntrega(int id);
    /**
     * Elimina la entrega; el controlador coordina el reinicio del pedido asociado.
     *
     * @param conn conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param id identificador del registro.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean eliminarEntrega(Connection conn, int id);
    /**
     * Vacía la tabla de entregas; no cambia los estados de los pedidos.
     *
     * @return true si se vació la tabla, incluso si ya estaba vacía.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean reiniciarEntregas();
}
