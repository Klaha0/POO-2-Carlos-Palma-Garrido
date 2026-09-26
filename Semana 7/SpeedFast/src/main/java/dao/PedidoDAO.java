package dao;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;


import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

public class PedidoDAO
{
    private final String QUERY_INSERT_PEDIDO = "INSERT INTO pedido (direccion, tipo, estado)  VALUES (?, ?, ?)";
    private final String QUERY_TRAER_TODOS = "SELECT * FROM pedido";
    private final String QUERY_TRAER_POR_ID = "SELECT * FROM pedido WHERE id = ?";
    private final String QUERY_UPDATE_PEDIDO = "UPDATE pedido SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
    private final String QUERY_DELETE_PEDIDO = "DELETE FROM pedido WHERE id = ?";
    
    public boolean guardar(String direccion, TipoPedido tipo, EstadoPedido estado) {
        
        try (var conexion = ConexionBD.getConexion();
             var ps = conexion.prepareStatement(QUERY_INSERT_PEDIDO)) {

            ps.setString(1, direccion);
            ps.setString(2, tipo.toString());
            ps.setString(3, estado.toString());

            int filas = ps.executeUpdate();
            return filas > 0;
            
        }catch (SQLException e){
            return false;
        }
    }

    public ArrayList<Pedido> listarTodos() {
        var pedidos = new ArrayList<Pedido>();

        try (var conexion = ConexionBD.getConexion();
             var ps = conexion.prepareStatement(QUERY_TRAER_TODOS);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String direccion = rs.getString("direccion");
                TipoPedido tipo = TipoPedido.valueOf(rs.getString("tipo"));
                EstadoPedido estado = EstadoPedido.valueOf(rs.getString("estado"));

                pedidos.add(new Pedido(id, direccion, estado, tipo));
            }
        } catch (SQLException e) {
            return null;
        }
        return pedidos;
    }

    public Pedido buscarPorId(int id) {
        try (var conexion = ConexionBD.getConexion()) {
            return buscarPorId(conexion, id);
        } catch (SQLException e) {
            return null;
        }
    }

    public Pedido buscarPorId(Connection conexion, int id) throws SQLException {
        try (var ps = conexion.prepareStatement(QUERY_TRAER_POR_ID + " FOR UPDATE")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Pedido(id, rs.getString("direccion"),
                        EstadoPedido.valueOf(rs.getString("estado")), TipoPedido.valueOf(rs.getString("tipo")));
            }
        }
    }

    public boolean actualizarPedido(Pedido pedido) {
        try (var conexion = ConexionBD.getConexion()) {
            return actualizarPedido(conexion, pedido);
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean actualizarPedido(Connection conexion, Pedido pedido) throws SQLException {
        try (var ps = conexion.prepareStatement(QUERY_UPDATE_PEDIDO)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().toString());
            ps.setString(3, pedido.getEstado().toString());
            ps.setInt(4, pedido.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarEstado(Connection conexion, int id, EstadoPedido estado) throws SQLException {
        try (var ps = conexion.prepareStatement("UPDATE pedido SET estado = ? WHERE id = ?")) {
            ps.setString(1, estado.toString());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminarPedido(int id) {
        try (var conexion = ConexionBD.getConexion();
             var ps = conexion.prepareStatement(QUERY_DELETE_PEDIDO)) {

            ps.setInt(1, id);
            int filasEliminadas = ps.executeUpdate();
            return filasEliminadas > 0;
        } catch (SQLException e) {
            return false;
        }
    }
}
