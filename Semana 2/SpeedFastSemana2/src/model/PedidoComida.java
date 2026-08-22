package model;

/**
 * Representa un pedido de comida en el sistema SpeedFast.
 * Hereda de Pedido y agrega funcionalidades específicas para tiempo de entrega de comida
 */
public class PedidoComida extends Pedido {
    private String comida;
    /**
     * Constructor de PedidoComida.
     * @param direccionEntrega la dirección de entrega
     * @param distanciaEnKm la distancia en kilómetros a la que se encuentra el pedido
     * @param comida el tipo de comida pedida
     */
    public PedidoComida(String direccionEntrega, int distanciaEnKm, String comida) {
        super(direccionEntrega, distanciaEnKm, "PedidoComida");
        this.comida = comida; 
    }

    /**
     * Calcula el tiempo estimado de entrega para un pedido de comida.
     * El tiempo se calcula como 15 minutos base más 2 minutos por cada kilómetro de distancia.
     * Muestra un mensaje con el tipo de comida y el tiempo estimado de entrega.
     */
    @Override
    public void calcularTiempoEntrega() {
        var mensaje ="Excelente!! su pedido: " + this.comida +"\nLlegará en un tiempo estimado de: " + (15 + (this.distanciaEnKm * 2)) + " minutos\n";
        System.out.println(mensaje);
    }
}
