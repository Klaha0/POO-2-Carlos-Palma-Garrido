package controlador;

import modelo.EstadoPedido;
import modelo.Pedido;
import modelo.Repartidor;
import modelo.TipoPedido;
import sincronizado.ZonaDeCarga;

import javax.swing.SwingUtilities;
import java.util.ArrayList;
import java.util.List;

/** Datos compartidos y coordinación de las entregas. Su API se utiliza desde el EDT. */
public class PedidoControlador {
    private final List<Pedido> pedidos = new ArrayList<>();
    private final List<Runnable> observadores = new ArrayList<>();
    private final List<Thread> hilosRepartidores = new ArrayList<>();
    private int repartidoresActivos;
    private boolean errorEnEntrega;
    private boolean entregando;
    private boolean cerrado;
    private String mensaje = "Registra pedidos para comenzar.";

    public Pedido registrarPedido(String direccion, TipoPedido tipo) {
        verificarHiloSwing();
        if (cerrado) {
            throw new IllegalStateException("El sistema está cerrado.");
        }
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona un tipo de pedido.");
        }
        Pedido pedido = new Pedido(direccion, EstadoPedido.PENDIENTE, tipo);
        pedidos.add(pedido);
        notificarCambios();
        return pedido;
    }

    public List<Pedido> getPedidos() {
        verificarHiloSwing();
        return List.copyOf(pedidos);
    }

    public boolean isEntregando() {
        return entregando;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void agregarObservador(Runnable observador) {
        verificarHiloSwing();
        observadores.add(observador);
    }

    public void quitarObservador(Runnable observador) {
        verificarHiloSwing();
        observadores.remove(observador);
    }

    public void iniciarEntregas() {
        verificarHiloSwing();
        if (entregando || cerrado) {
            return;
        }
        List<Pedido> pendientes = pedidos.stream()
                .filter(p -> p.getEstado() == EstadoPedido.PENDIENTE).toList();
        if (pendientes.isEmpty()) {
            throw new IllegalStateException("No hay pedidos pendientes. Registra un pedido primero.");
        }
        // Los registros nuevos quedan para el siguiente inicio, evitando carreras al vaciar la cola.
        ZonaDeCarga zona = new ZonaDeCarga(pendientes.size());
        pendientes.forEach(zona::agregarPedido);
        entregando = true;
        mensaje = "Entregas en curso.";
        repartidoresActivos = 3;
        errorEnEntrega = false;
        for (int i = 1; i <= 3; i++) {
            Repartidor repartidor = new Repartidor(zona, "Repartidor " + i,
                    () -> SwingUtilities.invokeLater(this::notificarCambios));
            Thread hilo = new Thread(() -> ejecutarRepartidor(repartidor), "repartidor-" + i);
            hilo.setDaemon(true);
            hilosRepartidores.add(hilo);
            hilo.start();
        }
        notificarCambios();
    }

    private void ejecutarRepartidor(Repartidor repartidor) {
        boolean correcto = true;
        try {
            repartidor.run();
        } catch (RuntimeException e) {
            correcto = false;
            e.printStackTrace();
        } finally {
            boolean resultado = correcto;
            // Las ventanas y el contador se actualizan en el hilo de eventos de Swing.
            SwingUtilities.invokeLater(() -> finalizarRepartidor(resultado));
        }
    }

    private void finalizarRepartidor(boolean correcto) {
        verificarHiloSwing();
        if (cerrado) {
            return;
        }
        if (!correcto) {
            errorEnEntrega = true;
        }
        repartidoresActivos--;
        if (repartidoresActivos == 0) {
            entregando = false;
            hilosRepartidores.clear();
            mensaje = errorEnEntrega ? "Error al completar las entregas." : "Entregas finalizadas.";
        }
        notificarCambios();
    }

    public void resetearPedidos() {
        verificarHiloSwing();
        if (cerrado) {
            throw new IllegalStateException("El sistema está cerrado.");
        }
        if (entregando) {
            throw new IllegalStateException("Espera a que terminen las entregas para usar Reset.");
        }
        for (Pedido pedido : pedidos) {
            pedido.setNombreRepartidor("Sin asignar");
            pedido.setEstado(EstadoPedido.PENDIENTE);
        }
        mensaje = "Pedidos listos para repartir otra vez.";
        notificarCambios();
    }

    public void cerrar() {
        verificarHiloSwing();
        cerrado = true;
        observadores.clear();
        for (Thread hilo : hilosRepartidores) {
            hilo.interrupt();
        }
    }

    private void notificarCambios() {
        verificarHiloSwing();
        if (!cerrado) {
            List.copyOf(observadores).forEach(Runnable::run);
        }
    }

    private void verificarHiloSwing() {
        if (!SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Utiliza el controlador desde el hilo de eventos de Swing.");
        }
    }
}
