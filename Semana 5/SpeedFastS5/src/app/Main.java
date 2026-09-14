package app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import model.EstadoPedido;
import model.NivelUrgencia;
import model.Pedido;
import model.Repartidor;
import sincronizado.ZonaDeCarga;

public class Main {

    public static void main(String[] args) {
        System.out.println("[Zona de carga inicializada]");

        ZonaDeCarga zonaDeCarga = new ZonaDeCarga(9);

        Pedido[] pedidos = {
                new Pedido("Santiago Centro", EstadoPedido.PENDIENTE, NivelUrgencia.BAJA),
                new Pedido("Providencia", EstadoPedido.PENDIENTE, NivelUrgencia.ALTA),
                new Pedido("Ñuñoa", EstadoPedido.PENDIENTE, NivelUrgencia.MEDIA),
                new Pedido("Recoleta", EstadoPedido.PENDIENTE, NivelUrgencia.ALTA),
                new Pedido("Las Condes", EstadoPedido.PENDIENTE, NivelUrgencia.BAJA),
                new Pedido("Maipú", EstadoPedido.PENDIENTE, NivelUrgencia.MEDIA),
                new Pedido("La Florida", EstadoPedido.PENDIENTE, NivelUrgencia.ALTA),
                new Pedido("Pudahuel", EstadoPedido.PENDIENTE, NivelUrgencia.BAJA),
                new Pedido("San Miguel", EstadoPedido.PENDIENTE, NivelUrgencia.MEDIA)
        };

        for (Pedido pedido : pedidos) {
            zonaDeCarga.agregarPedido(pedido);
        }

        ExecutorService executor = Executors.newFixedThreadPool(3);

        executor.execute(new Repartidor(zonaDeCarga, "Juan"));
        executor.execute(new Repartidor(zonaDeCarga, "Camila"));
        executor.execute(new Repartidor(zonaDeCarga, "Pedro"));

        executor.shutdown();

        try {
            boolean procesoFinalizado = executor.awaitTermination(1, TimeUnit.MINUTES);

            if (!procesoFinalizado) {
                executor.shutdownNow();
                System.out.println("La simulación superó el tiempo máximo de espera.");
                return;
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("La simulación fue interrumpida antes de finalizar.");
            return;
        }

        boolean todosEntregados = true;
        for (Pedido pedido : pedidos) {
            if (pedido.getEstado() != EstadoPedido.ENTREGADO) {
                todosEntregados = false;
                System.out.println("El pedido #" + pedido.getId()
                        + " no fue entregado." + "\n" +
                        "Estado: " + pedido.getEstado());
            }
        }

        if (todosEntregados) {
            System.out.println("Se termina la jornada, no hay más pedidos para entregar.");
            System.out.println("Todos los pedidos han sido entregados correctamente.");
        } else {
            System.out.println("La simulación terminó con pedidos pendientes.");
        }
    }
}
