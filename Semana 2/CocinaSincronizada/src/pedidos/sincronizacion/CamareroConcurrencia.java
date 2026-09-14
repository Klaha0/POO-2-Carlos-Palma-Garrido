package pedidos.sincronizacion;

import pedidos.Pedido;
import pedidos.PrioridadPedido;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Camarero que genera pedidos con prioridad y los envía a la cocina
 * de forma concurrente. Simula un hilo productor en un sistema multihilo.
 */
public class CamareroConcurrencia implements Runnable {

    private static final AtomicInteger contadorPedidos = new AtomicInteger(1);
    private final CocinaSincronizada cocina;
    private final String nombreCamarero;
    private final Random random = new Random();
    private final String[] platos = {"Lasaña", "Sopa", "Hamburguesa", "Ensalada", "Pizza"};

    public CamareroConcurrencia(CocinaSincronizada cocina, String nombreCamarero) {
        this.cocina = cocina;
        this.nombreCamarero = nombreCamarero;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                int id = contadorPedidos.getAndIncrement();
                String descripcion = platos[random.nextInt(platos.length)];
                PrioridadPedido prioridad = generarPrioridadAleatoria();
                Pedido pedido = new Pedido(id, descripcion, prioridad);

                System.out.println("[" + nombreCamarero + "] Generando pedido: " + pedido);
                cocina.recibirPedido(pedido);

                Thread.sleep(1000 + random.nextInt(2000)); // espera aleatoria entre pedidos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[" + nombreCamarero + "] Interrumpido. Finalizando tarea.");
            }
        }
    }

    /**
     * Genera una prioridad aleatoria para un pedido.
     * @return PrioridadPedido ALTA, MEDIA o BAJA
     */
    private PrioridadPedido generarPrioridadAleatoria() {
        int valor = random.nextInt(3);
        return switch (valor) {
            case 0 -> PrioridadPedido.ALTA;
            case 1 -> PrioridadPedido.MEDIA;
            default -> PrioridadPedido.BAJA;
        };
    }
}