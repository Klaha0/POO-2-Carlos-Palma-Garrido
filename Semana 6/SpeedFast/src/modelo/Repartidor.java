package modelo;

import java.util.Objects;
import java.util.Random;
import sincronizado.ZonaDeCarga;

/** Cada instancia consume pedidos de la misma cola desde un hilo independiente. */
public class Repartidor implements Runnable {
    private final ZonaDeCarga zonaDeCarga;
    private final String nombre;
    private final Runnable alCambiarPedido;
    private final Random random = new Random();

    public Repartidor(ZonaDeCarga zonaDeCarga, String nombre) {
        this(zonaDeCarga, nombre, () -> { });
    }

    public Repartidor(ZonaDeCarga zonaDeCarga, String nombre, Runnable alCambiarPedido) {
        this.zonaDeCarga = Objects.requireNonNull(zonaDeCarga);
        this.nombre = Objects.requireNonNull(nombre);
        this.alCambiarPedido = Objects.requireNonNull(alCambiarPedido);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            Pedido pedido;
            synchronized (zonaDeCarga) {
                pedido = zonaDeCarga.retirarPedido();
                if (pedido == null) {
                    return;
                }
                pedido.setNombreRepartidor(nombre);
                pedido.setEstado(EstadoPedido.EN_REPARTO);
            }
            informar(pedido);
            try {
                // La espera simula el viaje; nunca se ejecuta en el hilo de Swing.
                Thread.sleep((random.nextInt(6) + 1) * 1000L);
            } catch (InterruptedException e) {
                pedido.setNombreRepartidor("Sin asignar");
                pedido.setEstado(EstadoPedido.PENDIENTE);
                zonaDeCarga.agregarPedido(pedido);
                informar(pedido);
                Thread.currentThread().interrupt();
                return;
            }
            pedido.setEstado(EstadoPedido.ENTREGADO);
            informar(pedido);
        }
    }

    private void informar(Pedido pedido) {
        System.out.println("[" + nombre + "] Pedido #" + pedido.getId() + ": " + pedido.getEstado());
        alCambiarPedido.run();
    }
}
