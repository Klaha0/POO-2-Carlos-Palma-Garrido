package dao;

import java.sql.Connection;
import java.util.ArrayList;

import modelo.Repartidor;

public interface RepartidorDAO {
    /**
     * Inserta un registro mediante parámetros JDBC.
     *
     * @param repartidor repartidor con los datos validados.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean guardar(Repartidor repartidor);
    /**
     * Consulta todos los registros de la entidad.
     *
     * @return la lista de registros; vacía si no hay resultados.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    ArrayList<Repartidor> listarTodos();
    /**
     * Consulta un registro por su identificador.
     *
     * @param id identificador del registro.
     * @return el registro encontrado o null si no existe.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    Repartidor buscarPorId(int id);
    /**
     * Consulta un registro por su identificador.
     *
     * @param conn conexión abierta del llamador; el método no la cierra ni confirma la transacción.
     * @param id identificador del registro.
     * @return el registro encontrado o null si no existe.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    Repartidor buscarPorId(Connection conn, int id);
    /**
     * Actualiza el nombre del repartidor.
     *
     * @param repartidor repartidor con los datos validados.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean actualizarRepartidor(Repartidor repartidor);
    /**
     * Elimina el repartidor si las relaciones de MySQL lo permiten.
     *
     * @param id identificador del registro.
     * @return true si se modificaron filas; false si el registro no existe o no hubo cambios.
     * @throws IllegalStateException si falla el acceso a MySQL.
     */
    boolean eliminarRepartidor(int id);
}
