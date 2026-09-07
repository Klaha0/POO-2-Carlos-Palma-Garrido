package model;

/**
 * Clase concreta que representa un pedido de comida en la aplicación SpeedFast.
 * PedidoComida
 */
public class PedidoComida extends Pedido {
    private final String comida;

    /**
     * Construye un pedido nuevo de comida.
     * @param direccionEntrega dirección de destino
     * @param distanciaEnKm distancia hasta el destino
     * @param comida descripción de la comida
     */
    public PedidoComida(String direccionEntrega, int distanciaEnKm, String comida) {
        this(direccionEntrega, distanciaEnKm, "Creado", comida);
        agregarAPedidos();
    }

    /**
     * Construye un pedido de comida desde un registro persistido.
     * @param direccionEntrega dirección de destino
     * @param distanciaEnKm distancia hasta el destino
     * @param estadoPedido estado guardado
     * @param comida descripción de la comida
     */
    public PedidoComida(String direccionEntrega, int distanciaEnKm, String estadoPedido, String comida) {
        super("PedidoComida", direccionEntrega, distanciaEnKm, estadoPedido);
        this.comida = comida;
    }

    /** @return resumen de los datos del pedido de comida. */
    @Override
    public String toString() {
        return "Tipo pedido    : " + this.tipoPedido + "\n" +
                "ID pedido      : " + this.getIdPedido() + "\n" +
                "Dirección      : " + this.direccionEntrega + "\n" +
                "Distancia      : " + this.distanciaEnKm + " Km\n" +
                "Comida         : " + this.comida + "\n" +
                "Estado         : " + this.estadoPedido + "\n";
    }

    /** Calcula el tiempo de entrega: 15 minutos base más 2 por kilómetro. */
    @Override
    public void calcularTiempoEntrega() {
        int minutos = 15 + this.distanciaEnKm * 2;
        System.out.println("Tiempo estimado para comida: " + minutos + " minutos.");
    }

    /**
     * Asigna manualmente un repartidor para este pedido.
     * se especifica que es asignación manual para diferenciarla de la automática
     * al momento de mostrar la información en consola.
     * @param repartidor nombre del repartidor
     */
    @Override
    public void asignarRepartidor(Repartidor repartidor) {
        if (!esRepartidorValido(repartidor)) {
            return;
        }  
        System.out.println("--------------------------------");
        System.out.println("EL PEDIDO");
        this.mostrarResumen();
        System.out.println("Será entregado por " + repartidor.getNombre() + "\n");
        System.out.println("--------------------------------");
    }

    /** @return registro delimitado para guardar el pedido. */
    @Override
    public String persistir() {
        return this.tipoPedido + ";" + this.direccionEntrega + ";" + this.distanciaEnKm + ";" + this.estadoPedido + ";" + this.comida;
    }
}
