package controlador;

import java.sql.SQLException;
import java.util.List;

import dao.ConexionBD;
import dao.implementaciones.EntregaDAOImpl;
import dao.implementaciones.PedidoDAOImpl;
import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.TipoPedido;

/** Valida los datos del formulario y consulta el DAO. */
public class PedidoControlador {
    private final PedidoDAOImpl pedidoDAO = new PedidoDAOImpl();
    private final EntregaDAOImpl entregaDAO = new EntregaDAOImpl();

    
    /**
     * Valida y registra un pedido sin una entrega asociada.
     *
     * @param direccion dirección de entrega de hasta 100 caracteres.
     * @param estado estado inicial; no puede ser ENTREGADO.
     * @param tipo tipo de pedido seleccionado.
     * @return true si se guardó el registro; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
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
        if (direccion.trim().length() > 100) throw new IllegalArgumentException("La dirección admite hasta 100 caracteres.");
        if (estado == EstadoPedido.ENTREGADO) throw new IllegalArgumentException("Registra primero el pedido y luego su entrega con un repartidor.");
        return pedidoDAO.guardar(direccion.trim(), tipo, estado);
    }

    /**
     * Consulta un registro mediante su identificador.
     *
     * @param id identificador del registro.
     * @return el registro encontrado o null si no existe.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public Pedido buscarPorId(int id) { return pedidoDAO.buscarPorId(id); }

    /**
     * Consulta los registros persistidos para mostrarlos en los formularios.
     *
     * @return la lista de registros; vacía cuando no hay resultados.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public List<Pedido> getPedidos() {
        return pedidoDAO.listarTodos();
    }

    /**
     * Actualiza los datos respetando el estado de las entregas asociadas.
     *
     * @param pedido registro existente con los nuevos datos.
     * @return true si se actualizó el registro; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean actualizarPedido(Pedido pedido) {
        if (pedido == null || pedido.getId() <= 0) {
            throw new IllegalArgumentException("Selecciona un pedido válido.");
        }
        if (pedido.getDireccion() == null || pedido.getDireccion().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una dirección de entrega.");
        }
        if (pedido.getDireccion().trim().length() > 100) throw new IllegalArgumentException("La direccion admite hasta 100 caracteres.");
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
                if (pedido.getEstado() == EstadoPedido.ENTREGADO
                        && !entregaDAO.existeParaPedido(conexion, pedido.getId(), 0)) {
                    throw new IllegalStateException("Registra la entrega con un repartidor para marcar el pedido ENTREGADO.");
                }
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
     * Elimina el registro si las relaciones de la base de datos lo permiten.
     *
     * @param id identificador del registro que se desea eliminar.
     * @return true si se completó la eliminación; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean eliminarPedido(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de pedido inválido.");
        }
        return pedidoDAO.eliminarPedido(id);
    }

    /**
     * Elimina todas las entregas y pasa todos los pedidos a PENDIENTE en una transacción.
     *
     * @return true si ambos cambios se confirmaron.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean reiniciarPedidos(){
        // DELETE participa en la transaccion; TRUNCATE confirmaria cambios implicitamente.
        try (var conexion = ConexionBD.getConexion()) {
            conexion.setAutoCommit(false);
            try (var borrar = ConexionBD.preparar(conexion, "DELETE FROM entregas");
                 var reiniciar = ConexionBD.preparar(conexion, "UPDATE pedidos SET estado = ?")) {
                borrar.executeUpdate();
                reiniciar.setString(1, EstadoPedido.PENDIENTE.name());
                reiniciar.executeUpdate();
                conexion.commit();
                return true;
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                if (e instanceof SQLException sql) throw ConexionBD.errorPersistencia(sql);
                throw e;
            }
        } catch (SQLException e) {
            throw ConexionBD.errorPersistencia(e);
        }
    }
}
