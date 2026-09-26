package controlador;

import java.util.ArrayList;

import dao.RepartidorDAO;
import modelo.Repartidor;

public class RepartidorControlador {
    private final RepartidorDAO repartidorDAO = new RepartidorDAO();

    
    public boolean registrarRepartidor(Repartidor repartidor) {
        if (repartidor.getNombre() == null || repartidor.getNombre().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un nombre de repartidor.");
        }
        return repartidorDAO.guardar(repartidor);
    }

    public ArrayList<Repartidor> getRepartidores() {
        return repartidorDAO.listarTodos();
    }        

    public boolean actualizarRepartidor(Repartidor repartidor) {        
        if (repartidor.getNombre() == null || repartidor.getNombre().isBlank()) {
            throw new IllegalArgumentException("Debe ingresar un nombre de repartidor.");
        }

        return repartidorDAO.actualizarRepartidor(repartidor);
    }

    public boolean eliminarRepartidor(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("ID de repartidor inválido.");
        }
        
        return repartidorDAO.eliminarRepartidor(id);
    }
}
