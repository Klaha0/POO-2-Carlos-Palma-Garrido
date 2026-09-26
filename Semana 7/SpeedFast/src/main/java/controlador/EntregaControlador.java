package controlador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import dao.ConexionBD;
import dao.EntregaDAO;
import dao.PedidoDAO;
import dao.RepartidorDAO;
import modelo.Entrega;
import modelo.EstadoPedido;
import modelo.Pedido;

/** Valida las entregas y coordina su registro con el estado del pedido. */
public class EntregaControlador {
    private final EntregaDAO entregaDAO = new EntregaDAO();
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    public boolean registrarEntrega(Entrega entrega) {
        validarDatos(entrega);
        return guardarEntrega(entrega, false);
    }

    public boolean actualizarEntrega(Entrega entrega) {
        validarDatos(entrega);
        if (entrega.getId() <= 0) throw new IllegalArgumentException("ID de entrega inválido.");
        return guardarEntrega(entrega, true);
    }

    private void validarDatos(Entrega entrega) {
        if (entrega == null) throw new IllegalArgumentException("Selecciona una entrega válida.");
        if (entrega.getIdPedido() <= 0) throw new IllegalArgumentException("ID de pedido inválido.");
        if (entrega.getIdRepartidor() <= 0) throw new IllegalArgumentException("ID de repartidor inválido.");
    }

    private boolean guardarEntrega(Entrega entrega, boolean editar) {
        // Una misma conexión permite validar y guardar todo, o deshacerlo si falla.
        try (var conexion = ConexionBD.getConexion()) {
            conexion.setAutoCommit(false);
            try {
                Entrega anterior = editar ? entregaDAO.buscarPorId(conexion, entrega.getId()) : null;
                if (editar && anterior == null) {
                    throw new IllegalStateException("La entrega ya no existe. Refresca el listado.");
                }
                Pedido pedido = pedidoDAO.buscarPorId(conexion, entrega.getIdPedido());
                if (pedido == null) throw new IllegalStateException("El pedido ya no existe. Refresca el listado.");
                boolean mismoPedido = editar && anterior.getIdPedido() == pedido.getId();
                if (!mismoPedido && pedido.getEstado() == EstadoPedido.ENTREGADO) {
                    throw new IllegalStateException("El pedido ya está ENTREGADO y no puede entregarse otra vez.");
                }
                if (entregaDAO.existeParaPedido(conexion, pedido.getId(), editar ? entrega.getId() : 0)) {
                    throw new IllegalStateException("El pedido ya tiene una entrega registrada.");
                }
                if (repartidorDAO.buscarPorId(conexion, entrega.getIdRepartidor()) == null) {
                    throw new IllegalStateException("El repartidor ya no existe. Actualiza las opciones.");
                }
                boolean guardado = editar ? entregaDAO.actualizarEntrega(conexion, entrega)
                        : entregaDAO.guardar(conexion, entrega.getIdPedido(), entrega.getIdRepartidor());
                if (!guardado) {
                    conexion.rollback();
                    return false;
                }
                if (pedido.getEstado() != EstadoPedido.ENTREGADO
                        && !pedidoDAO.actualizarEstado(conexion, pedido.getId(), EstadoPedido.ENTREGADO)) {
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

    public boolean puedeRegistrarEntrega(Pedido pedido, List<Entrega> entregas) {
        return pedido.getEstado() != EstadoPedido.ENTREGADO
                && entregas.stream().noneMatch(e -> e.getIdPedido() == pedido.getId());
    }

    public ArrayList<Entrega> getEntregas() { return entregaDAO.listarTodas(); }

    public Entrega buscarPorId(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID de entrega inválido.");
        return entregaDAO.buscarPorId(id);
    }

    public boolean eliminarEntrega(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID de entrega inválido.");
        return entregaDAO.eliminarEntrega(id);
    }
}
