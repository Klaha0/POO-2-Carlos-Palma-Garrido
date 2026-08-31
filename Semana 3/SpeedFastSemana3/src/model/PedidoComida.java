package model;

import static java.util.Objects.isNull;

/**
 * Clase concreta que representa un pedido de comida en la aplicación SpeedFast.
 * PedidoComida
 */
public class PedidoComida extends Pedido {
    /** Repartidor que atiende los pedidos de comida. */
    private static final String REPARTIDOR_DE_TURNO = "Luis Díaz";

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
                "Repartidor     : " + (isNull(this.repartidorAsignado) ? "Sin asignar" : this.repartidorAsignado) + "\n" +
                "Estado         : " + this.estadoPedido + "\n";
    }

    /** Calcula el tiempo de entrega: 15 minutos base más 2 por kilómetro. */
    @Override
    public void calcularTiempoEntrega() {
        int minutos = 15 + this.distanciaEnKm * 2;
        System.out.println("Tiempo estimado para comida: " + minutos + " minutos.\n");
    }

    /**
     * Asigna automáticamente el repartidor que atiende los pedidos de comida.
     */
    @Override
    public void asignarRepartidor() {
        System.out.println("Buscando un repartidor para su pedido de comida...");
        this.repartidorAsignado = REPARTIDOR_DE_TURNO;
        System.out.println("Asignación automática: " + this.repartidorAsignado + " para el pedido de comida.\n");
    }

    /**
     * Asigna manualmente un repartidor para este pedido.
     * se especifica que es asignación manual para diferenciarla de la automática
     * al momento de mostrar la información en consola.
     * @param nombreRepartidor nombre del repartidor
     */
    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        if (!esRepartidorValido(nombreRepartidor)) {
            return;
        }
        this.repartidorAsignado = nombreRepartidor;
        System.out.println("Asignación manual:\nSu comida ID: " + getIdPedido() + " será entregada por " + this.repartidorAsignado + "\n");
    }

    /** @return registro delimitado para guardar el pedido. */
    @Override
    public String persistir() {
        return this.tipoPedido + ";" + this.direccionEntrega + ";" + this.distanciaEnKm + ";" + this.estadoPedido + ";" + this.comida + ";" + (isNull(this.repartidorAsignado) ? "" : this.repartidorAsignado);
    }
}
