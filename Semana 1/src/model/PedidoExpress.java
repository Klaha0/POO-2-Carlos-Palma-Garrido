package model;

import java.util.ArrayList;

/**
 * Representa un pedido express en el sistema SpeedFast.
 * Hereda de Pedido y agrega funcionalidades específicas para entregas express,
 * incluyendo búsqueda de repartidor cercano disponible y lista de productos.
 */
public class PedidoExpress extends Pedido {
    /** Indica si hay un repartidor cercano disponible para entrega inmediata */
    private boolean repartidorCercanoDisponible;
    /** Lista de productos incluidos en el pedido express */
    private ArrayList<String> listaProductos;

    /**
     * Constructor de PedidoExpress.
     * @param direccionEntrega la dirección de entrega
     * @param repartidorCercanoDisponible indica disponibilidad de repartidor cercano
     * @param listaProductos lista de productos a entregar
     */
    public PedidoExpress(String direccionEntrega, boolean repartidorCercanoDisponible, ArrayList<String> listaProductos) {
        super(direccionEntrega);
        this.repartidorCercanoDisponible = repartidorCercanoDisponible;
        this.listaProductos = listaProductos;
    }

    /**
     * Verifica si hay un repartidor cercano disponible.
     * @return true si hay disponibilidad, false en caso contrario
     */
    public boolean isRepartidorCercanoDisponible() {
        return repartidorCercanoDisponible;
    }

    /**
     * Establece la disponibilidad de repartidor cercano.
     * @param repartidorCercanoDisponible true si hay repartidor disponible
     */
    public void setRepartidorCercanoDisponible(boolean repartidorCercanoDisponible) {
        this.repartidorCercanoDisponible = repartidorCercanoDisponible;
    }

    /**
     * Obtiene la lista de productos del pedido express.
     * @return lista de productos
     */
    public ArrayList<String> getListaProductos() {
        return listaProductos;
    }

    /**
     * Establece la lista de productos para el pedido express.
     * @param listaProductos nueva lista de productos
     */
    public void setListaProductos(ArrayList<String> listaProductos) {
        this.listaProductos = listaProductos;
    }

    /**
     * sobreescribe el método AsignarRepartidor
     * tambien utiliza el método de la super clase
     * @return un mensaje con el estado de asignación
     */
    @Override
    public String AsignarRepartidor() {
        return "[Pedido Express]\n" +
                super.AsignarRepartidor() +
                "Repartidor más cercano con disponibilidad inmediata encontrado.\n";
    }

    /**
     * Asigna un repartidor específico al pedido express.
     * Solo asigna si hay un repartidor cercano disponible.
     * @param nombreRepartidor el nombre del repartidor asignado
     * @return un mensaje con los detalles de la asignación o un mensaje de no disponibilidad
     */
    public String AsignarRepartidor(String nombreRepartidor) {
        if(!repartidorCercanoDisponible)
            return "Lo sentimos mucho, pero no tenemos un repartidor cercano disponible";

         StringBuilder sb = new StringBuilder();
         sb.append(AsignarRepartidor());
         sb.append("→ Su encomienda ID: ");
         sb.append(super.getIdPedido()+ "\n");
         sb.append("→ con los siguientes productos:\n");
         for (String producto : listaProductos) {
             if(producto.equals(listaProductos.getLast()))
                 sb.append("- " + producto);
             else
             sb.append("- " + producto + "\n");
         }
         sb.append("\nha sido asignado a → ");
         sb.append(nombreRepartidor + "\n");
         sb.append("¡¡LLegaremos en unos minutos!!\n");
         return sb.toString();
    }
}