package sincronizado;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import modelo.Pedido;

public class ZonaDeCarga {
    private final PriorityBlockingQueue<Pedido> colaPedidos;

    /**
     * Crea una zona de carga que atiende primero los pedidos más urgentes.
     * Si dos pedidos tienen la misma urgencia, se atiende primero el de menor ID.
     *
     * @param capacidadInicial cantidad inicial de espacios reservados por la cola
     */
    public ZonaDeCarga(int capacidadInicial) {
        if (capacidadInicial <= 0) {
            throw new IllegalArgumentException("La capacidad inicial debe ser mayor que cero.");
        }

        Comparator<Pedido> comparadorPrioridad = (pedido1, pedido2) -> {
            int comparacionUrgencia = Integer.compare(
                    pedido2.getNivelUrgencia().getPrioridad(),
                    pedido1.getNivelUrgencia().getPrioridad()
            );

            if (comparacionUrgencia != 0) {
                return comparacionUrgencia;
            }

            return Integer.compare(pedido1.getId(), pedido2.getId());
        };

        colaPedidos = new PriorityBlockingQueue<>(
                capacidadInicial,
                comparadorPrioridad
        );
    }

    public synchronized void agregarPedido(Pedido pedido) {
        colaPedidos.put(pedido);

        System.out.println("Pedido #" + pedido.getId()
                + " agregado. Destino: " + pedido.getDireccionEntrega()
                + ". Estado: " + pedido.getEstado()
                + ". Urgencia: " + pedido.getNivelUrgencia());
    }

    public synchronized Pedido retirarPedido() {
        return colaPedidos.poll();
    }
}
