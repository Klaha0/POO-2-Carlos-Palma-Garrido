package model;

/**
 * Super clase que representa un pedido genérico en el sistema SpeedFast.
 * Todos los tipos de pedidos heredan de esta clase.
 * Mantiene un contador estático para asignar IDs únicos a cada pedido.
 */
public class Pedido {
    /** Contador estático para generar IDs únicos */
    static int asignaId = 0;
    /** Identificador único del pedido */
    private int idPedido;
    /** Dirección de entrega del pedido */
    protected String direccionEntrega;

    /**
     * Constructor de la clase Pedido.
     * Inicializa el pedido con una dirección de entrega y asigna un ID único al pedido
     * @param direccionEntrega la dirección donde se entregará el pedido
     */
    public Pedido(String direccionEntrega) {

        this.direccionEntrega = direccionEntrega;
        this.idPedido = Pedido.asignaId;
        Pedido.asignaId++;
    }

    /**
     * Obtiene el identificador único del pedido.
     * @return el ID del pedido
     */
    public int getIdPedido() {
        return idPedido;
    }

    /**
     * Obtiene la dirección de entrega del pedido.
     * @return la dirección de entrega
     */
    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    /**
     * Establece una nueva dirección de entrega para el pedido.
     * @param direccionEntrega la nueva dirección de entrega
     */
    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = direccionEntrega;
    }

    /**
     * Asigna un repartidor al pedido.
     * Este es un método base que puede ser sobrescrito por subclases.
     * @return un mensaje indicando que se está asignando un repartidor
     */
    public String AsignarRepartidor() {
        return "Asignando repartidor...\n";
    }

}
