package model;

import static java.util.Objects.isNull;

/** Representa un pedido de encomienda dentro del sistema SpeedFast. */
public class PedidoEncomienda extends Pedido {
    /** Repartidor que atiende las encomiendas. */
    private static final String REPARTIDOR_DE_TURNO = "Carlos Soto";

    private final String encomienda;

    /**
     * Construye una encomienda nueva.
     * @param direccionEntrega dirección de destino
     * @param distanciaEnKm distancia hasta el destino
     * @param encomienda descripción de la encomienda
     */
    public PedidoEncomienda(String direccionEntrega, int distanciaEnKm, String encomienda) {
        this(direccionEntrega, distanciaEnKm, "Creado", encomienda);
        agregarAPedidos();
    }

    /**
     * Construye una encomienda desde un registro persistido.
     * @param direccionEntrega dirección de destino
     * @param distanciaEnKm distancia hasta el destino
     * @param estadoPedido estado guardado
     * @param encomienda descripción de la encomienda
     */
    public PedidoEncomienda(String direccionEntrega, int distanciaEnKm, String estadoPedido, String encomienda) {
        super("PedidoEncomienda", direccionEntrega, distanciaEnKm, estadoPedido);
        this.encomienda = encomienda;
    }

    /** @return resumen de los datos de la encomienda. */
    @Override
    public String toString() {
        return "Tipo pedido    : " + this.tipoPedido + "\n" +
                "ID pedido      : " + this.getIdPedido() + "\n" +
                "Dirección      : " + this.direccionEntrega + "\n" +
                "Distancia      : " + this.distanciaEnKm + " Km\n" +
                "Encomienda     : " + this.encomienda + "\n" +
                "Repartidor     : " + (isNull(this.repartidorAsignado) ? "Sin asignar" : this.repartidorAsignado) + "\n" +
                "Estado         : " + this.estadoPedido + "\n";
    }

    /** Calcula el tiempo de entrega: 20 minutos base más 1,5 por kilómetro. */
    @Override
    public void calcularTiempoEntrega() {
        int minutos = 20 + (int) (this.distanciaEnKm * 1.5);
        System.out.println("Tiempo estimado de encomienda: " + minutos + " minutos.\n");
    }

    /**
     * Asigna automáticamente el repartidor que atiende las encomiendas.
     */
    @Override
    public void asignarRepartidor() {
        System.out.println("Buscando un repartidor para su encomienda...");
        this.repartidorAsignado = REPARTIDOR_DE_TURNO;
        System.out.println("Asignación automática: " + this.repartidorAsignado + " para la encomienda.\n");
    }

    /**
     * Asigna manualmente un repartidor para la encomienda.
     * @param nombreRepartidor nombre del repartidor
     */
    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        if (!esRepartidorValido(nombreRepartidor)) {
            return;
        }
        this.repartidorAsignado = nombreRepartidor;
        System.out.println("Asignación manual:\nSu encomienda ID: " + getIdPedido() + " será entregada por " + this.repartidorAsignado + "\n");
    }

    /** @return registro delimitado para guardar la encomienda. */
    @Override
    public String persistir() {
        return this.tipoPedido + ";" + this.direccionEntrega + ";" + this.distanciaEnKm + ";" + this.estadoPedido + ";" + this.encomienda + ";" + (isNull(this.repartidorAsignado) ? "" : this.repartidorAsignado);
    }
}
