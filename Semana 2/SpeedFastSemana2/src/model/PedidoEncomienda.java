package model;

/**
 * Representa un pedido de encomienda en el sistema SpeedFast.
 * Hereda de Pedido y agrega funcionalidades específicas paratiempo de entrega de encomienda
 */
public class PedidoEncomienda extends Pedido {
    private String encomienda;
    /**
     * Constructor de PedidoEncomienda.
     * @param direccionEntrega la dirección de entrega
     * @param distanciaEnKm la distancia en kilómetros a la que se encuentra el pedido
     * @param encomienda el tipo de encomienda pedida
     */
    public PedidoEncomienda(String direccionEntrega, int distanciaEnKm, String encomienda) {
        super(direccionEntrega, distanciaEnKm, "PedidoEncomienda");
        this.encomienda = encomienda;
    }

    /**
     * Calcula el tiempo estimado de entrega para un pedido de encomienda.
     * El tiempo se calcula como 20 minutos base más 1.5 minutos por cada kilómetro de distancia.
     * Muestra un mensaje con el tipo de encomienda y el tiempo estimado de entrega
     */
    @Override
    public void calcularTiempoEntrega() {
        String mensaje = "Excelente!! su encomienda: " + this.encomienda + "\nLlegará en un tiempo estimado de: " + (20 + (int)(this.distanciaEnKm * 1.5 )) + " minutos\n";
        System.out.println(mensaje);
    }
}
