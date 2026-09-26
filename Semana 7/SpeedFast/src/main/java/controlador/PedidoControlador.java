package controlador;

import java.util.List;
import java.sql.SQLException;
import dao.ConexionBD;
import dao.EntregaDAO;

import dao.PedidoDAO;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

/** Valida los datos del formulario y consulta el DAO. */
public class PedidoControlador {
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final EntregaDAO entregaDAO = new EntregaDAO();

    
    public boolean registrarPedido(String direccion, EstadoPedido estado, TipoPedido tipo) {
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una dirección de entrega.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona un tipo de pedido.");
        }
        if (estado == null) {
            throw new IllegalArgumentException("Selecciona un estado de pedido.");
        }
        return pedidoDAO.guardar(direccion.trim(), tipo, estado);
    }

    public List<Pedido> getPedidos() {
        return pedidoDAO.listarTodos();
    }

    public boolean actualizarPedido(Pedido pedido) {
        if (pedido == null || pedido.getId() <= 0) {
            throw new IllegalArgumentException("Selecciona un pedido válido.");
        }
        if (pedido.getDireccion() == null || pedido.getDireccion().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una dirección de entrega.");
        }
        if (pedido.getTipo() == null) {
            throw new IllegalArgumentException("Selecciona un tipo de pedido.");
        }
        if (pedido.getEstado() == null) {
            throw new IllegalArgumentException("Selecciona un estado de pedido.");
        }
        try (var conexion = ConexionBD.getConexion()) {
            conexion.setAutoCommit(false);
            try {
                Pedido actual = pedidoDAO.buscarPorId(conexion, pedido.getId());
                if (actual == null) throw new IllegalStateException("El pedido ya no existe. Refresca el listado.");
                if (pedido.getEstado() != EstadoPedido.ENTREGADO
                        && (actual.getEstado() == EstadoPedido.ENTREGADO
                        || entregaDAO.existeParaPedido(conexion, pedido.getId(), 0))) {
                    throw new IllegalStateException("Un pedido entregado o con una entrega registrada "
                            + "debe conservar el estado ENTREGADO.");
                }
                if (!pedidoDAO.actualizarPedido(conexion, pedido)) {
                    conexion.rollback();
                    return false;
                }
                conexion.commit();
                return true;
            } catch (SQLException e) {
                conexion.rollback();
                return false;
            } catch (RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean eliminarPedido(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de pedido inválido.");
        }
        return pedidoDAO.eliminarPedido(id);
    }
}
