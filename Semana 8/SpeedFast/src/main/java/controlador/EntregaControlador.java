package controlador;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dao.ConexionBD;
import dao.implementaciones.EntregaDAOImpl;
import dao.implementaciones.PedidoDAOImpl;
import dao.implementaciones.RepartidorDAOImpl;
import modelo.Entrega;
import modelo.EstadoPedido;
import modelo.Pedido;

/** Valida las entregas y coordina su registro con el estado del pedido. */
public class EntregaControlador {
    private final EntregaDAOImpl entregaDAO = new EntregaDAOImpl();
    private final PedidoDAOImpl pedidoDAO = new PedidoDAOImpl();
    private final RepartidorDAOImpl repartidorDAO = new RepartidorDAOImpl();

    /**
     * Registra la entrega y marca el pedido ENTREGADO en una misma transacción.
     *
     * @param entrega datos del registro que se desea guardar.
     * @return true si se guardó el registro; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean registrarEntrega(Entrega entrega) {
        validarDatos(entrega);
        return guardarEntrega(entrega, false);
    }

    /**
     * Cambia únicamente el repartidor y conserva el pedido, la fecha y la hora.
     *
     * @param entrega registro existente con los nuevos datos.
     * @return true si se actualizó el registro; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean actualizarEntrega(Entrega entrega) {
        validarDatos(entrega);
        if (entrega.getId() <= 0) throw new IllegalArgumentException("ID de entrega inválido.");
        return guardarEntrega(entrega, true);
    }

    /**
     * Comprueba que la entrega tenga identificadores válidos de pedido y repartidor.
     *
     * @param entrega datos que se desean persistir.
     * @throws IllegalArgumentException si falta la entrega o alguno de sus identificadores es inválido.
     */
    private void validarDatos(Entrega entrega) {
        if (entrega == null) throw new IllegalArgumentException("Selecciona una entrega válida.");
        if (entrega.getIdPedido() <= 0) throw new IllegalArgumentException("ID de pedido inválido.");
        if (entrega.getIdRepartidor() <= 0) throw new IllegalArgumentException("ID de repartidor inválido.");
    }

    /**
     * Coordina las validaciones y el guardado con el estado del pedido; revierte todo si falla.
     *
     * @param entrega entrega que se desea registrar o modificar.
     * @param editar true para actualizar una entrega existente; false para registrar una nueva.
     * @return true si la transacción se confirmó; false si no se modificaron filas.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
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
                if (editar && anterior.getIdPedido() != entrega.getIdPedido()) {
                    throw new IllegalArgumentException("Solo se puede modificar el repartidor de la entrega.");
                }
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
                throw ConexionBD.errorPersistencia(e);
            } catch (RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /**
     * Comprueba si un pedido está disponible para una nueva entrega.
     *
     * @param pedido pedido que se desea ofrecer en el combo.
     * @param entregas entregas registradas que se han consultado.
     * @return true si no está ENTREGADO y no tiene una entrega asociada.
     */
    public boolean puedeRegistrarEntrega(Pedido pedido, List<Entrega> entregas) {
        return pedido.getEstado() != EstadoPedido.ENTREGADO
                && entregas.stream().noneMatch(e -> e.getIdPedido() == pedido.getId());
    }

    /**
     * Consulta los registros persistidos para mostrarlos en los formularios.
     *
     * @return la lista de registros; vacía cuando no hay resultados.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public ArrayList<Entrega> getEntregas() { return entregaDAO.listarTodas(); }

    /**
     * Consulta un registro mediante su identificador.
     *
     * @param id identificador del registro.
     * @return el registro encontrado o null si no existe.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public Entrega buscarPorId(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID de entrega inválido.");
        return entregaDAO.buscarPorId(id);
    }

    /**
     * Elimina la entrega y reinicia su pedido a PENDIENTE de forma atómica.
     *
     * @param id identificador del registro que se desea eliminar.
     * @return true si se completó la eliminación; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean eliminarEntrega(int id) {
        if (id <= 0) throw new IllegalArgumentException("ID de entrega inválido.");
        // Eliminacion y reinicio se confirman juntos; cualquier fallo deshace ambos cambios.
        try (var conexion = ConexionBD.getConexion()) {
            conexion.setAutoCommit(false);
            try {
                Entrega entrega = entregaDAO.buscarPorId(conexion, id);
                if (entrega == null) throw new IllegalStateException("La entrega ya no existe. Refresca el listado.");
                if (pedidoDAO.buscarPorId(conexion, entrega.getIdPedido()) == null) {
                    throw new IllegalStateException("El pedido asociado ya no existe. Refresca el listado.");
                }
                if (!entregaDAO.eliminarEntrega(conexion, id)
                        || !pedidoDAO.actualizarEstado(conexion, entrega.getIdPedido(), EstadoPedido.PENDIENTE)) {
                    conexion.rollback();
                    return false;
                }
                conexion.commit();
                return true;
            } catch (SQLException e) {
                conexion.rollback();
                throw ConexionBD.errorPersistencia(e);
            } catch (RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }

    /**
     * Vacía únicamente la tabla de entregas; no cambia los estados de los pedidos.
     *
     * @return true si se vació la tabla.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean reiniciarEntregas(){
        return  entregaDAO.reiniciarEntregas();
    }
}
