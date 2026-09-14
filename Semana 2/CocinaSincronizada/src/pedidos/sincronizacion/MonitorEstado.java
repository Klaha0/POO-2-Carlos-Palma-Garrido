package pedidos.sincronizacion;

/**
 * Hilo que monitorea el estado actual de la cocina concurrente.
 * Informa periódicamente cuántos pedidos están pendientes en la cola.
 */
public class MonitorEstado implements Runnable {

    private final CocinaSincronizada cocina;
    private volatile boolean activo = true;

    public MonitorEstado(CocinaSincronizada cocina) {
        this.cocina = cocina;
    }

    /**
     * Permite detener el monitoreo desde fuera del hilo.
     */
    public void detener() {
        activo = false;
    }

    @Override
    public void run() {
        while (activo) {
            int pedidosPendientes = cocina.getCantidadPedidosPendientes();
            System.out.println("[Monitor] Pedidos pendientes en cocina: " + pedidosPendientes);
            try {
                Thread.sleep(3000); // monitorea cada 3 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Monitor] Interrumpido. Finalizando monitoreo.");
                break;
            }
        }
        System.out.println("[Monitor] Finalizado.");
    }
}