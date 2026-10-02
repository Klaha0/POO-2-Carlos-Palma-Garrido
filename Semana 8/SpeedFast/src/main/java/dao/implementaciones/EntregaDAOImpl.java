package dao.implementaciones;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

import dao.ConexionBD;
import dao.EntregaDAO;
import modelo.Entrega;

public class EntregaDAOImpl implements EntregaDAO{
    private final String QUERY_INSERT_ENTREGA = "INSERT INTO entregas (id_pedido, id_repartidor, fecha, hora)" +
    " VALUES (?, ?, DATE(NOW()), TIME(NOW()))";
    private final String QUERY_TRAER_TODOS = "SELECT * FROM entregas";
    private final String QUERY_TRAER_POR_ID = "SELECT * FROM entregas WHERE id = ?";
    private final String QUERY_UPDATE_ENTREGA = "UPDATE entregas SET id_repartidor = ? WHERE id = ?";
    private final String QUERY_DELETE_ENTREGA = "DELETE FROM entregas WHERE id = ?";
    private final String EXISTE_PARA_PEDIDO = "SELECT id FROM entregas WHERE id_pedido = ? AND id <> ? FOR UPDATE";
    private final String QUERY_REINICIAR_ENTREGAS = "TRUNCATE entregas";

    /** {@inheritDoc} */
    @Override
    public boolean guardar(int idPedido, int idRepartidor) {
        try (var conn = ConexionBD.getConexion()) {
            return guardar(conn, idPedido, idRepartidor);
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    // Esta variante usa la conexión de la operación que coordina el controlador.
    /** {@inheritDoc} */
    @Override
    public boolean guardar(Connection conn, int idPedido, int idRepartidor){
        try (var stmt = ConexionBD.preparar(conn, QUERY_INSERT_ENTREGA)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, idRepartidor);
            return stmt.executeUpdate() > 0;
        }catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean existeParaPedido(Connection conn, int idPedido, int idEntregaExcluida){
        try (var stmt = ConexionBD.preparar(conn, EXISTE_PARA_PEDIDO)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, idEntregaExcluida);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }
        }catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<Entrega> listarTodas() {
        var entregas = new ArrayList<Entrega>();

        try (var conn = ConexionBD.getConexion();
             var stmt = ConexionBD.preparar(conn, QUERY_TRAER_TODOS);
             var resultSet = stmt.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int idPedido = resultSet.getInt("id_pedido");
                int idRepartidor = resultSet.getInt("id_repartidor");
                String fecha = resultSet.getString("fecha");
                String hora = resultSet.getString("hora");

                entregas.add(new Entrega(id, idPedido, idRepartidor, fecha, hora));
            }
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
        return entregas;
    }

    /** {@inheritDoc} */
    @Override
    public Entrega buscarPorId(int id) {
        try (var conn = ConexionBD.getConexion()) {
            return buscarPorId(conn, id);
        }catch (SQLException e){
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Entrega buscarPorId(Connection conn, int id){
        try (var stmt = ConexionBD.preparar(conn, QUERY_TRAER_POR_ID + " FOR UPDATE")) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return new Entrega(id, rs.getInt("id_pedido"), rs.getInt("id_repartidor"),
                        rs.getString("fecha"), rs.getString("hora"));
            }
        }catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizarEntrega(Entrega entrega) {
        try (var conn = ConexionBD.getConexion()) {
            return actualizarEntrega(conn, entrega);
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizarEntrega(Connection conn, Entrega entrega){
        try (var stmt = ConexionBD.preparar(conn, QUERY_UPDATE_ENTREGA)) {
            stmt.setInt(1, entrega.getIdRepartidor());
            stmt.setInt(2, entrega.getId());
            return stmt.executeUpdate() > 0;
        }catch(SQLException e){
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean eliminarEntrega(int id){
        try (var conn = ConexionBD.getConexion()) {
            return eliminarEntrega(conn, id);
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean eliminarEntrega(Connection conn, int id){
        try (var stmt = ConexionBD.preparar(conn, QUERY_DELETE_ENTREGA)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean reiniciarEntregas() {
        try(var conn = ConexionBD.getConexion();
            var stmt = ConexionBD.preparar(conn, QUERY_REINICIAR_ENTREGAS)){
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
        return true;
    }
}
