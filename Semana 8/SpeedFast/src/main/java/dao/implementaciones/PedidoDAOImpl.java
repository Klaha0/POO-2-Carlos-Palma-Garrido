package dao.implementaciones;

import java.sql.SQLException;
import java.sql.Connection;
import java.util.ArrayList;

import dao.ConexionBD;
import dao.PedidoDAO;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

public class PedidoDAOImpl implements PedidoDAO{
    private final String QUERY_INSERT_PEDIDO = "INSERT INTO pedidos (direccion, tipo, estado)  VALUES (?, ?, ?)";
    private final String QUERY_TRAER_TODOS = "SELECT * FROM pedidos";
    private final String QUERY_TRAER_POR_ID = "SELECT * FROM pedidos WHERE id = ?";
    private final String QUERY_UPDATE_PEDIDO = "UPDATE pedidos SET direccion = ?, tipo = ?, estado = ? WHERE id = ?";
    private final String QUERY_UPDATE_PEDIDO2 = "UPDATE pedidos SET estado = ? WHERE id = ?";
    private final String QUERY_DELETE_PEDIDO = "DELETE FROM pedidos WHERE id = ?";
    private final String QUERY_REINICIAR_PEDIDOS = "UPDATE pedidos SET estado = ?";
    
    /** {@inheritDoc} */
    @Override
    public boolean guardar(String direccion, TipoPedido tipo, EstadoPedido estado) {
        
        try (var conexion = ConexionBD.getConexion();
             var ps = ConexionBD.preparar(conexion, QUERY_INSERT_PEDIDO)) {

            ps.setString(1, direccion);
            ps.setString(2, tipo.toString());
            ps.setString(3, estado.toString());

            int filas = ps.executeUpdate();
            return filas > 0;
            
        }catch (SQLException e){
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public ArrayList<Pedido> listarTodos() {
        var pedidos = new ArrayList<Pedido>();

        try (var conexion = ConexionBD.getConexion();
             var ps = ConexionBD.preparar(conexion, QUERY_TRAER_TODOS);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String direccion = rs.getString("direccion");
                TipoPedido tipo = TipoPedido.valueOf(rs.getString("tipo"));
                EstadoPedido estado = EstadoPedido.valueOf(rs.getString("estado"));

                pedidos.add(new Pedido(id, direccion, estado, tipo));
            }
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
        return pedidos;
    }

    /** {@inheritDoc} */
    @Override
    public Pedido buscarPorId(int id) {
        try (var conexion = ConexionBD.getConexion()) {
            return buscarPorId(conexion, id);
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public Pedido buscarPorId(Connection conexion, int id){
        try (var ps = ConexionBD.preparar(conexion, QUERY_TRAER_POR_ID + " FOR UPDATE")) {
            ps.setInt(1, id);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new Pedido(id, rs.getString("direccion"),
                        EstadoPedido.valueOf(rs.getString("estado")), TipoPedido.valueOf(rs.getString("tipo")));
            }
        }catch(SQLException e){
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizarPedido(Pedido pedido) {
        try (var conexion = ConexionBD.getConexion()) {
            return actualizarPedido(conexion, pedido);
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizarPedido(Connection conexion, Pedido pedido){
        try (var ps = ConexionBD.preparar(conexion, QUERY_UPDATE_PEDIDO)) {
            ps.setString(1, pedido.getDireccion());
            ps.setString(2, pedido.getTipo().toString());
            ps.setString(3, pedido.getEstado().toString());
            ps.setInt(4, pedido.getId());
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean actualizarEstado(Connection conexion, int id, EstadoPedido estado){
        try (var ps = ConexionBD.preparar(conexion, QUERY_UPDATE_PEDIDO2)) {
            ps.setString(1, estado.toString());
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }catch(SQLException e){
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean eliminarPedido(int id) {
        try (var conexion = ConexionBD.getConexion();
             var ps = ConexionBD.preparar(conexion, QUERY_DELETE_PEDIDO)) {

            ps.setInt(1, id);
            int filasEliminadas = ps.executeUpdate();
            return filasEliminadas > 0;
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean reiniciarPedidos() {
        try(var conn = ConexionBD.getConexion();
            var ps = ConexionBD.preparar(conn, QUERY_REINICIAR_PEDIDOS)){

            ps.setString(1,"PENDIENTE");
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }
}
