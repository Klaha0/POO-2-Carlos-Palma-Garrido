package model;

/**
 * Super clase que representa un pedido genérico en el sistema SpeedFast.
 * Todos los tipos de pedidos heredan de esta clase.
 * Mantiene un contador estático para asignar IDs únicos a cada pedido.
 */
public abstract class  Pedido {
    /** Contador estático para generar IDs únicos */
    static int asignaId = 10000;
    /** Identificador único del pedido */
    private final int idPedido;
    private String direccionEntrega;
    protected int distanciaEnKm;
    private String tipoPedido;

    /**
     * Constructor de la clase Pedido.
     * Inicializa el pedido con una dirección de entrega y asigna un ID único al pedido
     * @param direccionEntrega la dirección donde se entregará el pedido
     * @param distanciaEnKm la distancia en kilómetros a la que se encuentra el pedido
     * @param tipoPedido el tipo de pedido
     */
    protected Pedido(String direccionEntrega, int distanciaEnKm, String tipoPedido) {

        this.direccionEntrega = direccionEntrega;
        this.idPedido = Pedido.asignaId;
        this.distanciaEnKm = distanciaEnKm;
        this.tipoPedido = tipoPedido;
        Pedido.asignaId++;
    }

    /**
     * Muestra un resumen del pedido.
     */
    public void mostrarResumen() {
        String resumen =  this.tipoPedido + " # " + this.idPedido + "\n" +
                            "Dirección      : " + this.direccionEntrega +  "\n" +
                            "Distancia      : " + this.distanciaEnKm + "Km";
        System.out.println(resumen);
    }
    
    /**
     * Método abstracto que debe ser implementado por las subclases para calcular el tiempo estimado de entrega.
     */
    public abstract void calcularTiempoEntrega();
}
