package dao;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

import modelo.Entrega;

public class EntregaDAO {
    private final String QUERY_INSERT_ENTREGA = "INSERT INTO entrega (id_pedido, id_repartidor, fecha, hora)" +
    " VALUES (?, ?, DATE(NOW()), TIME(NOW()))";
    private final String QUERY_TRAER_TODOS = "SELECT * FROM entrega";
    private final String QUERY_TRAER_POR_ID = "SELECT * FROM entrega WHERE id = ?";
    private final String QUERY_UPDATE_ENTREGA = "UPDATE entrega SET id_pedido = ?, id_repartidor = ?," +
    "fecha = DATE(NOW()), hora = TIME(NOW()) WHERE id = ?";
    private final String QUERY_DELETE_ENTREGA = "DELETE FROM entrega WHERE id = ?";

    public boolean guardar(int idPedido, int idRepartidor) {
        try (var conn = ConexionBD.getConexion()) {
            return guardar(conn, idPedido, idRepartidor);
        } catch (SQLException e) {
            return false;
        }
    }

    // Esta variante usa la conexión de la operación que coordina el controlador.
    public boolean guardar(Connection conn, int idPedido, int idRepartidor) throws SQLException {
        try (var stmt = conn.prepareStatement(QUERY_INSERT_ENTREGA)) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, idRepartidor);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean existeParaPedido(Connection conn, int idPedido, int idEntregaExcluida) throws SQLException {
        try (var stmt = conn.prepareStatement(
                "SELECT id FROM entrega WHERE id_pedido = ? AND id <> ? FOR UPDATE")) {
            stmt.setInt(1, idPedido);
            stmt.setInt(2, idEntregaExcluida);
            try (var rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    public ArrayList<Entrega> listarTodas() {
        var entregas = new ArrayList<Entrega>();

        try (var conn = ConexionBD.getConexion();
             var stmt = conn.prepareStatement(QUERY_TRAER_TODOS);
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
            return null;
        }
        return entregas;
    }

    public Entrega buscarPorId(int id) {
        try (var conn = ConexionBD.getConexion()) {
            return buscarPorId(conn, id);
        } catch (SQLException e) {
            return null;
        }
    }

    public Entrega buscarPorId(Connection conn, int id) throws SQLException {
        try (var stmt = conn.prepareStatement(QUERY_TRAER_POR_ID + " FOR UPDATE")) {
            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (!rs.next()) return null;
                return new Entrega(id, rs.getInt("id_pedido"), rs.getInt("id_repartidor"),
                        rs.getString("fecha"), rs.getString("hora"));
            }
        }
    }

    public boolean actualizarEntrega(Entrega entrega) {
        try (var conn = ConexionBD.getConexion()) {
            return actualizarEntrega(conn, entrega);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean actualizarEntrega(Connection conn, Entrega entrega) throws SQLException {
        try (var stmt = conn.prepareStatement(QUERY_UPDATE_ENTREGA)) {
            stmt.setInt(1, entrega.getIdPedido());
            stmt.setInt(2, entrega.getIdRepartidor());
            stmt.setInt(3, entrega.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean eliminarEntrega(int id){
        try (var conn = ConexionBD.getConexion();
             var stmt = conn.prepareStatement(QUERY_DELETE_ENTREGA)) {

            stmt.setInt(1, id);
            int filasEliminadas = stmt.executeUpdate();
            return filasEliminadas > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
