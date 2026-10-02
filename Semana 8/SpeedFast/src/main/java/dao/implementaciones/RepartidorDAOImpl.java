package dao.implementaciones;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

import dao.ConexionBD;
import dao.RepartidorDAO;
import modelo.Repartidor;

public class RepartidorDAOImpl implements RepartidorDAO{
    private final String QUERY_INSERT_REPARTIDOR = "INSERT INTO repartidores (nombre) VALUES (?)";
    private final String QUERY_TRAER_TODOS = "SELECT id, nombre FROM repartidores";
    private final String QUERY_TRAER_POR_ID = "SELECT id, nombre FROM repartidores WHERE id = ?";
    private final String QUERY_ACTUALIZAR_REPARTIDOR = "UPDATE repartidores SET nombre = ? WHERE id = ?";
    private final String QUERY_ELIMINAR_REPARTIDOR = "DELETE FROM repartidores WHERE id = ?";

    /** {@inheritDoc} */
    @Override
    public boolean guardar(Repartidor repartidor) {
        try (var conn = ConexionBD.getConexion();
             var stmt = ConexionBD.preparar(conn, QUERY_INSERT_REPARTIDOR)) {

            stmt.setString(1, repartidor.getNombre());
            int filas = stmt.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<Repartidor> listarTodos(){
        var repartidores = new ArrayList<Repartidor>();

        try (var conn = ConexionBD.getConexion();
             var stmt = ConexionBD.preparar(conn, QUERY_TRAER_TODOS);
             var resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {     
                int id = resultSet.getInt("id");           
                String nombre = resultSet.getString("nombre");
                repartidores.add(new Repartidor(id, nombre));
            }
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
        return repartidores;
    }

    /** {@inheritDoc} */
    @Override
    public Repartidor buscarPorId(int id) {
        try (var conn = ConexionBD.getConexion()) {
            return buscarPorId(conn, id);
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Repartidor buscarPorId(Connection conn, int id){
        try (var stmt = ConexionBD.preparar(conn, QUERY_TRAER_POR_ID + " FOR UPDATE")) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                return rs.next() ? new Repartidor(id, rs.getString("nombre")) : null;
            }
        }catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizarRepartidor(Repartidor repartidor) {
        try (var conn = ConexionBD.getConexion();
             var stmt = ConexionBD.preparar(conn, QUERY_ACTUALIZAR_REPARTIDOR)) {

            stmt.setString(1, repartidor.getNombre());
            stmt.setInt(2, repartidor.getId());
            int filasActualizadas = stmt.executeUpdate();
            return filasActualizadas > 0;
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean eliminarRepartidor(int id) {
        try (var conn = ConexionBD.getConexion();
             var stmt = ConexionBD.preparar(conn, QUERY_ELIMINAR_REPARTIDOR)) {

            stmt.setInt(1, id);
            int filasEliminadas = stmt.executeUpdate();
            return filasEliminadas > 0;
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }
}
