package model;

import java.util.Random;
import sincronizado.ZonaDeCarga;

public class Repartidor implements Runnable {
    private final ZonaDeCarga zonaDeCarga;
    private final String nombre;
    private final Random random = new Random();
    

    public Repartidor(ZonaDeCarga zonaDeCarga, String nombre) {
        this.zonaDeCarga = zonaDeCarga;
        this.nombre = nombre;
    }

    @Override
    public void run() {
        while (true) {
            Pedido pedido;

            synchronized (zonaDeCarga) {
                pedido = zonaDeCarga.retirarPedido();

                if (pedido != null) {
                    pedido.setEstado(EstadoPedido.EN_REPARTO);
                    System.out.println("[Repartidor - " + this.nombre + "] Retiró el pedido #" +
                            pedido.getId() + "\n" +
                            "Destino: " + pedido.getDireccionEntrega() + "\n" +
                            "Urgencia: " + pedido.getNivelUrgencia() + "\n" +
                            "Estado: " + pedido.getEstado());
                }
            }

            if (pedido == null) {
                return;
            }

            try {
                Thread.sleep(random.nextInt(3000) + 1000);
            } catch (InterruptedException e) {
                pedido.setEstado(EstadoPedido.PENDIENTE);
                zonaDeCarga.agregarPedido(pedido);
                Thread.currentThread().interrupt();

                System.out.println("[Repartidor - " + this.nombre
                        + "] Entrega interrumpida.\nEl pedido #" + pedido.getId()
                        + " regresó a la zona de carga.");
                return;
            }

            pedido.setEstado(EstadoPedido.ENTREGADO);
            System.out.println("[Repartidor - " + this.nombre + "] Entregó el pedido #"
                    + pedido.getId() + "\n"+
                    "Destino: " + pedido.getDireccionEntrega() + "\n" +
                    "Urgencia: " + pedido.getNivelUrgencia() + "\n" +
                    "Estado: " + pedido.getEstado());
        }
    }
}

