package model;

import static java.util.Objects.isNull;

/** Representa un pedido express dentro del sistema SpeedFast. */
public class PedidoExpress extends Pedido {
    /** Repartidor que atiende los pedidos express. */
    private static final String REPARTIDOR_DE_TURNO = "Daniela Tapia";

    private final int cantidadProductos;

    /**
     * Construye un pedido express nuevo.
     * @param direccionEntrega dirección de destino
     * @param distanciaEnKm distancia hasta el destino
     * @param cantidadProductos cantidad de productos
     */
    public PedidoExpress(String direccionEntrega, int distanciaEnKm, int cantidadProductos) {
        this(direccionEntrega, distanciaEnKm, "Creado", cantidadProductos);
        agregarAPedidos();
    }

    /**
     * Construye un pedido express desde un registro persistido.
     * @param direccionEntrega dirección de destino
     * @param distanciaEnKm distancia hasta el destino
     * @param estadoPedido estado guardado
     * @param cantidadProductos cantidad de productos
     */
    public PedidoExpress(String direccionEntrega, int distanciaEnKm, String estadoPedido, int cantidadProductos) {
        super("PedidoExpress", direccionEntrega, distanciaEnKm, estadoPedido);
        this.cantidadProductos = cantidadProductos;
    }

    /** @return resumen de los datos del pedido express. */
    @Override
    public String toString() {
        return "Tipo pedido    : " + this.tipoPedido + "\n" +
                "ID pedido      : " + this.getIdPedido() + "\n" +
                "Dirección      : " + this.direccionEntrega + "\n" +
                "Distancia      : " + this.distanciaEnKm + " Km\n" +
                "Cantidad       : " + this.cantidadProductos + "\n" +
                "Repartidor     : " + (isNull(this.repartidorAsignado) ? "Sin asignar" : this.repartidorAsignado) + "\n" +
                "Estado         : " + this.estadoPedido + "\n";
    }

    /** Calcula el tiempo express: 10 minutos base y 15 cuando supera los 5 km. */
    @Override
    public void calcularTiempoEntrega() {
        int minutos = this.distanciaEnKm > 5 ? 15 : 10;
        System.out.println("Tiempo estimado express: " + minutos + " minutos.\n");
    }

    /**
     * Asigna automáticamente el repartidor que atiende los pedidos express.
     */
    @Override
    public void asignarRepartidor() {
        System.out.println("Buscando un repartidor para su pedido express...");
        this.repartidorAsignado = REPARTIDOR_DE_TURNO;
        System.out.println("Asignación automática: " + this.repartidorAsignado + " para el pedido express.\n");
    }

    /**
     * Asigna manualmente un repartidor para este pedido.
     * @param nombreRepartidor nombre del repartidor
     */
    @Override
    public void asignarRepartidor(String nombreRepartidor) {
        if (!esRepartidorValido(nombreRepartidor)) {
            return;
        }
        this.repartidorAsignado = nombreRepartidor;
        System.out.println("Asignación manual:\nSu pedido express ID: " + getIdPedido() + " será entregado por " + this.repartidorAsignado + "\n");
    }

    /** @return registro delimitado para guardar el pedido. */
    @Override
    public String persistir() {
        return this.tipoPedido + ";" + this.direccionEntrega + ";" + this.distanciaEnKm + ";" + this.estadoPedido + ";" + this.cantidadProductos + ";" + (isNull(this.repartidorAsignado) ? "" : this.repartidorAsignado);
    }
}
