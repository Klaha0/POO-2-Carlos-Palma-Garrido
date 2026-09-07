package model;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;

/**
 * Repartidor de SpeedFast. Cada repartidor recorre su propia ruta de pedidos,
 * por lo que implementa Runnable y puede ejecutarse como un hilo independiente
 * en paralelo con los demás repartidores.
 */
public class Repartidor implements Runnable {
    private final String nombre;
    private final PriorityBlockingQueue<Pedido> pedidosAsignados;
    private final Random random = new Random();

    /**
     * Construye un repartidor con su ruta todavía vacía.
     * @param nombre nombre con el que se identifica en consola
     */
    public Repartidor(String nombre) {
        this.nombre = nombre;
        this.pedidosAsignados = new PriorityBlockingQueue<>();
    }

    /** @return nombre del repartidor. */
    public String getNombre() {
        return this.nombre;
    }

    /**
     * Recorre la ruta entregando un pedido tras otro y avisando su avance.
     * El método no se sincroniza a propósito: cada repartidor trabaja sobre su
     * propia cola y esa cola ya es segura entre hilos, de modo que tomar aquí
     * el monitor del objeto sólo dejaría esperando a quien quisiera asignar un
     * pedido nuevo mientras el repartidor está en ruta.
     */
    @Override
    public void run() {
        try {
            // poll() devuelve null cuando la ruta se agotó, así que la misma
            // extracción sirve de condición y no queda una ventana entre
            // preguntar si quedan pedidos y sacar el siguiente.
            Pedido pedido;
            while ((pedido = this.pedidosAsignados.poll()) != null) {
                System.out.println(this.nombre + " va en camino con el pedido: " + pedido.getIdPedido());
                Thread.sleep(generarPausa()); 
                System.out.println(this.nombre + " ya está llegando al destino del pedido: " + pedido.getIdPedido());
                Thread.sleep(generarPausa());
                System.out.println(this.nombre + " ha entregado el pedido: " + pedido.getIdPedido());
                pedido.entregar(); 
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("El repartidor " + this.nombre + " ha sido interrumpido.");
        }
    }

    /**
     * Agrega un pedido a la ruta del repartidor. La cola de prioridad ordena
     * los pedidos por cercanía y es segura entre hilos, por lo que se puede
     * asignar trabajo incluso con el repartidor ya en ruta.
     * @param pedido pedido que se incorpora a la ruta
     */
    public void asignarPedido(Pedido pedido) {
        this.pedidosAsignados.put(pedido);
    }

    /** @return pausa aleatoria de entre 1 y 5 segundos que simula el trayecto. */
    private int generarPausa() {
        return this.random.nextInt(4000) + 1000;
    }
}
