package controlador;

import java.util.ArrayList;

import dao.implementaciones.RepartidorDAOImpl;
import modelo.Repartidor;

public class RepartidorControlador {
    private final RepartidorDAOImpl repartidorDAO = new RepartidorDAOImpl();

    
    /**
     * Valida y registra un repartidor.
     *
     * @param repartidor datos del registro que se desea guardar.
     * @return true si se guardó el registro; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean registrarRepartidor(Repartidor repartidor) {
        if (repartidor == null) throw new IllegalArgumentException("Selecciona un repartidor valido.");
        if (repartidor.getNombre() == null || repartidor.getNombre().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un nombre de repartidor.");
        }
        validarNombre(repartidor);
        return repartidorDAO.guardar(repartidor);
    }

    /**
     * Limita el nombre antes de persistirlo, incluso fuera del formulario.
     *
     * @param repartidor repartidor con un nombre obligatorio ya validado.
     * @throws IllegalArgumentException si el nombre supera los 100 caracteres.
     */
    private void validarNombre(Repartidor repartidor) {
        if (repartidor.getNombre().trim().length() > 100) {
            throw new IllegalArgumentException("El nombre admite hasta 100 caracteres.");
        }
    }

    /**
     * Consulta los registros persistidos para mostrarlos en los formularios.
     *
     * @return la lista de registros; vacía cuando no hay resultados.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public ArrayList<Repartidor> getRepartidores() {
        return repartidorDAO.listarTodos();
    }        

    /**
     * Valida y actualiza el nombre del repartidor.
     *
     * @param repartidor registro existente con los nuevos datos.
     * @return true si se actualizó el registro; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean actualizarRepartidor(Repartidor repartidor) {        
        if (repartidor == null) throw new IllegalArgumentException("Selecciona un repartidor valido.");
        if (repartidor.getNombre() == null || repartidor.getNombre().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un nombre de repartidor.");
        }

        validarNombre(repartidor);
        if (repartidor.getId() <= 0) throw new IllegalArgumentException("ID de repartidor invalido.");
        return repartidorDAO.actualizarRepartidor(repartidor);
    }

    /**
     * Elimina el registro si las relaciones de la base de datos lo permiten.
     *
     * @param id identificador del registro que se desea eliminar.
     * @return true si se completó la eliminación; false si no se modificaron filas.
     * @throws IllegalArgumentException si los datos obligatorios o el identificador no son válidos.
     * @throws IllegalStateException si falla el acceso a MySQL o no se puede completar la operación.
     */
    public boolean eliminarRepartidor(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de repartidor inválido.");
        }
        
        return repartidorDAO.eliminarRepartidor(id);
    }
}
