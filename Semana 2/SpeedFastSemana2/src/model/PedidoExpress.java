package model;

/**
 * Representa un pedido express en el sistema SpeedFast.
 * Hereda de Pedido y agrega funcionalidades específicas para tiempo de entrega express,
 */
public class PedidoExpress extends Pedido {
    private int cantidadProductos;

   /**
     * Constructor de PedidoExpress.
     * @param direccionEntrega la dirección de entrega
     * @param distanciaEnKm la distancia en kilómetros a la que se encuentra el pedido
     * @param producto el producto pedido
     */
    public PedidoExpress(String direccionEntrega, int distanciaEnKm, int cantidadProductos) {
        super(direccionEntrega, distanciaEnKm, "PedidoExpress");
        this.cantidadProductos = cantidadProductos;
    }

    /**
     * Calcula el tiempo estimado de entrega para un pedido express.
     * El tiempo se calcula como 10 minutos base más 5 minutos adicionales 
     * si la distancia es mayor a 5 kilómetros.
     * Muestra un mensaje con la cantidad de productos y el tiempo estimado de entrega.
     */
    @Override
    public void calcularTiempoEntrega() {
        int tiempoBase = 10; // Tiempo base para pedidos express
        int tiempoLlegada = distanciaEnKm>5 ? (tiempoBase + 5) : tiempoBase;

        String mensaje = "Excelente!! sus " + this.cantidadProductos + " productos\n"+
        "Llegarán en un tiempo estimado de: " + tiempoLlegada + " minutos\n";
        System.out.println(mensaje);
    }
}