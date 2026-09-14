package pedidos.sincronizacion;

import pedidos.Pedido;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Cocina sincronizada que usa una cola de prioridad para atender pedidos
 * según su urgencia. Limita la cantidad de pedidos en espera y protege el acceso
 * con un ReentrantLock.
 */
public class CocinaSincronizada {

    private final PriorityBlockingQueue<Pedido> colaPedidos;
    private final int capacidadMaxima;
    private final ReentrantLock lock = new ReentrantLock();

    public CocinaSincronizada(int capacidadMaxima) {
        this.capacidadMaxima = capacidadMaxima;
        this.colaPedidos = new PriorityBlockingQueue<>();
    }

    /**
     * Agrega un pedido a la cocina si no ha alcanzado su capacidad.
     * @param pedido el pedido a recibir
     * @return true si el pedido fue aceptado, false si fue rechazado
     */
    public boolean recibirPedido(Pedido pedido) {
        lock.lock();
        try {
            if (colaPedidos.size() >= capacidadMaxima) {
                System.out.println("[Cocina] Capacidad máxima alcanzada. Rechazando pedido: " + pedido);
                return false;
            }
            colaPedidos.add(pedido);
            System.out.println("[Cocina] Pedido recibido: " + pedido);
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Toma y remueve el pedido más prioritario de la cola.
     * @return el pedido procesado, o null si no había pedidos
     */
    public Pedido procesarPedido() {
        lock.lock();
        try {
            Pedido pedido = colaPedidos.poll();
            if (pedido != null) {
                System.out.println("[Cocina] Procesando: " + pedido);
            }
            return pedido;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Retorna el número actual de pedidos en espera.
     * @return cantidad de pedidos pendientes
     */
    public int getCantidadPedidosPendientes() {
        return colaPedidos.size();
    }
}