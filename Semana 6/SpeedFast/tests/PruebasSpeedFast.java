import controlador.PedidoControlador;
import modelo.*;
import sincronizado.ZonaDeCarga;
import vista.*;

import javax.swing.*;
import java.awt.Window;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/** Pruebas ejecutables sin dependencias externas; las ventanas no se muestran. */
public class PruebasSpeedFast {
    public static void main(String[] args) throws Exception {
        try {
            probarPrioridad();
            probarInterrupcion();
            probarFlujoAsincrono();
            System.out.println("OK: prioridad, validación, formularios, tabla, concurrencia e interrupción.");
        } finally {
            SwingUtilities.invokeAndWait(() -> {
                for (Window ventana : Window.getWindows()) ventana.dispose();
            });
        }
    }

    private static void probarPrioridad() {
        ZonaDeCarga cola = new ZonaDeCarga(4);
        Pedido baja = pedido("Baja", TipoPedido.ENCOMIENDA);
        Pedido alta1 = pedido("Alta 1", TipoPedido.EXPRESS);
        Pedido alta2 = pedido("Alta 2", TipoPedido.EXPRESS);
        Pedido media = pedido("Media", TipoPedido.COMIDA);
        comprobar(baja.getNivelUrgencia() == NivelUrgencia.BAJA, "Encomienda asigna urgencia baja");
        comprobar(alta1.getNivelUrgencia() == NivelUrgencia.ALTA, "Express asigna urgencia alta");
        comprobar(media.getNivelUrgencia() == NivelUrgencia.MEDIA, "Comida asigna urgencia media");
        List.of(baja, alta2, media, alta1).forEach(cola::agregarPedido);
        comprobar(cola.retirarPedido() == alta1, "Primero la urgencia alta y menor ID");
        comprobar(cola.retirarPedido() == alta2, "Desempate por ID");
        comprobar(cola.retirarPedido() == media, "Luego urgencia media");
        comprobar(cola.retirarPedido() == baja && cola.retirarPedido() == null, "Cola vacía");
    }

    private static void probarInterrupcion() throws Exception {
        ZonaDeCarga cola = new ZonaDeCarga(1);
        Pedido pedido = pedido("Interrupción", TipoPedido.COMIDA);
        cola.agregarPedido(pedido);
        CountDownLatch inicio = new CountDownLatch(1);
        Thread hilo = new Thread(new Repartidor(cola, "Prueba", inicio::countDown));
        hilo.start();
        comprobar(inicio.await(2, TimeUnit.SECONDS), "El repartidor inició");
        hilo.interrupt();
        hilo.join(2000);
        comprobar(!hilo.isAlive(), "El hilo termina al interrumpirse");
        comprobar(pedido.getEstado() == EstadoPedido.PENDIENTE, "La interrupción devuelve el pedido");
        comprobar(cola.retirarPedido() == pedido && cola.retirarPedido() == null, "Se devuelve una sola vez");
        comprobar(pedido.getNombreRepartidor().equals("Sin asignar"), "Se libera la asignación");
    }

    private static void probarFlujoAsincrono() throws Exception {
        PedidoControlador controlador = new PedidoControlador();
        CountDownLatch enRuta = new CountDownLatch(1);
        CountDownLatch finalizado = new CountDownLatch(1);
        AtomicBoolean callbacksEnEdt = new AtomicBoolean(true);
        AtomicReference<VentanaListaPedidos> listado = new AtomicReference<>();
        try {
            SwingUtilities.invokeAndWait(() -> {
                esperarError(() -> controlador.iniciarEntregas());
                esperarError(() -> controlador.registrarPedido("   ", TipoPedido.COMIDA));
                esperarError(() -> controlador.registrarPedido("Destino", null));
                comprobar(controlador.getPedidos().isEmpty(), "Los datos inválidos no se guardan");
                // Construir las tres ventanas detecta errores de binding o de instrumentación .form.
                VentanaPrincipal principal = new VentanaPrincipal();
                comprobar(principal.getContentPane() != null, "Formulario principal enlazado");
                principal.dispose();
                VentanaRegistroPedido registro = new VentanaRegistroPedido(controlador);
                comprobar(!campo(registro, "txtId", JTextField.class).isEditable(), "ID automático");
                comprobar(campo(registro, "cmbTipoPedido", JComboBox.class).getItemCount() == 3, "Tres tipos");
                JComboBox<?> tipos = campo(registro, "cmbTipoPedido", JComboBox.class);
                JLabel urgencia = campo(registro, "lblUrgenciaValor", JLabel.class);
                tipos.setSelectedItem(TipoPedido.EXPRESS);
                comprobar(urgencia.getText().startsWith("ALTA"), "Formulario muestra urgencia Express");
                tipos.setSelectedItem(TipoPedido.COMIDA);
                comprobar(urgencia.getText().startsWith("MEDIA"), "Formulario muestra urgencia Comida");
                tipos.setSelectedItem(TipoPedido.ENCOMIENDA);
                comprobar(urgencia.getText().startsWith("BAJA"), "Formulario muestra urgencia Encomienda");
                tipos.setSelectedIndex(-1);
                comprobar(urgencia.getText().equals("Selecciona un tipo de pedido"), "Se limpia la urgencia sin tipo");
                registro.dispose();
                listado.set(new VentanaListaPedidos(controlador));
                comprobar(!campo(listado.get(), "btnReset", JButton.class).isEnabled(), "Reset sin pedidos deshabilitado");
                controlador.registrarPedido("  Baja 1  ", TipoPedido.ENCOMIENDA);
                controlador.registrarPedido("Baja 2", TipoPedido.ENCOMIENDA);
                controlador.registrarPedido("Media", TipoPedido.COMIDA);
                for (int i = 1; i <= 3; i++) {
                    controlador.registrarPedido("Alta " + i, TipoPedido.EXPRESS);
                }
                comprobar(controlador.getPedidos().get(0).getDireccionEntrega().equals("Baja 1"), "Dirección normalizada");
                comprobar(tabla(listado.get()).getRowCount() == 6, "Tabla se actualiza al registrar");
                comprobar(!tabla(listado.get()).isCellEditable(0, 0), "Tabla de solo lectura");
                controlador.agregarObservador(() -> {
                    if (!SwingUtilities.isEventDispatchThread()) callbacksEnEdt.set(false);
                    long activos = controlador.getPedidos().stream()
                            .filter(p -> p.getEstado() == EstadoPedido.EN_REPARTO).count();
                    if (activos == 3) enRuta.countDown();
                    if (!controlador.isEntregando()) finalizado.countDown();
                });
                controlador.iniciarEntregas();
                controlador.iniciarEntregas(); // No debe duplicar repartidores ni entregas.
                comprobar(!campo(listado.get(), "btnReset", JButton.class).isEnabled(), "Reset deshabilitado durante reparto");
                esperarError(controlador::resetearPedidos);
            });
            comprobar(enRuta.await(3, TimeUnit.SECONDS), "Tres entregas concurrentes");
            CountDownLatch interfazLibre = new CountDownLatch(1);
            SwingUtilities.invokeLater(() -> {
                comprobar(controlador.getPedidos().stream()
                        .filter(p -> p.getEstado() == EstadoPedido.EN_REPARTO)
                        .allMatch(p -> p.getNivelUrgencia() == NivelUrgencia.ALTA), "Alta prioridad sale primero");
                controlador.registrarPedido("Nuevo durante entrega", TipoPedido.COMIDA);
                interfazLibre.countDown();
            });
            comprobar(interfazLibre.await(1, TimeUnit.SECONDS), "Swing sigue respondiendo durante la entrega");
            comprobar(finalizado.await(25, TimeUnit.SECONDS), "Finaliza el lote");
            SwingUtilities.invokeAndWait(() -> {
                List<Pedido> pedidos = controlador.getPedidos();
                comprobar(pedidos.subList(0, 6).stream().allMatch(p -> p.getEstado() == EstadoPedido.ENTREGADO),
                        "Todos los pedidos iniciales entregados");
                comprobar(pedidos.get(6).getEstado() == EstadoPedido.PENDIENTE, "Nuevo pedido espera próximo lote");
                comprobar(tabla(listado.get()).getRowCount() == 7, "Tabla conserva todos los pedidos");
                comprobar(tabla(listado.get()).getValueAt(0, 4).equals("ENTREGADO"), "Tabla refleja estado final");
                comprobar(callbacksEnEdt.get(), "Toda actualización se realiza en EDT");
            });
            CountDownLatch segundoLote = new CountDownLatch(1);
            SwingUtilities.invokeAndWait(() -> {
                controlador.agregarObservador(() -> {
                    if (!controlador.isEntregando()) segundoLote.countDown();
                });
                controlador.iniciarEntregas();
            });
            comprobar(segundoLote.await(8, TimeUnit.SECONDS), "Se puede iniciar otro lote");
            SwingUtilities.invokeAndWait(() -> {
                comprobar(controlador.getPedidos().stream().allMatch(p -> p.getEstado() == EstadoPedido.ENTREGADO),
                        "El segundo lote termina sin reenviar pedidos entregados");
                esperarError(() -> controlador.iniciarEntregas());
            });
            CountDownLatch repeticion = new CountDownLatch(1);
            SwingUtilities.invokeAndWait(() -> {
                List<Pedido> originales = controlador.getPedidos();
                JButton reset = campo(listado.get(), "btnReset", JButton.class);
                comprobar(reset.isEnabled(), "Reset habilitado al finalizar");
                reset.doClick();
                comprobar(controlador.getPedidos().equals(originales), "Reset conserva los mismos pedidos e ID");
                comprobar(originales.stream().allMatch(p -> p.getEstado() == EstadoPedido.PENDIENTE
                        && p.getNombreRepartidor().equals("Sin asignar")), "Reset restaura estado y repartidor");
                comprobar(tabla(listado.get()).getValueAt(0, 4).equals("PENDIENTE"), "Reset actualiza la tabla");
                controlador.agregarObservador(() -> {
                    if (!controlador.isEntregando()) repeticion.countDown();
                });
                controlador.iniciarEntregas();
            });
            comprobar(repeticion.await(30, TimeUnit.SECONDS), "El reparto puede repetirse después de Reset");
            SwingUtilities.invokeAndWait(() -> comprobar(controlador.getPedidos().stream()
                    .allMatch(p -> p.getEstado() == EstadoPedido.ENTREGADO), "Todos se entregan nuevamente"));
        } finally {
            SwingUtilities.invokeAndWait(controlador::cerrar);
        }
    }

    private static Pedido pedido(String direccion, TipoPedido tipo) {
        return new Pedido(direccion, EstadoPedido.PENDIENTE, tipo);
    }

    private static JTable tabla(VentanaListaPedidos ventana) {
        return campo(ventana, "tablaPedidos", JTable.class);
    }

    private static <T> T campo(Object objeto, String nombre, Class<T> tipo) {
        try {
            Field campo = objeto.getClass().getDeclaredField(nombre);
            campo.setAccessible(true);
            return tipo.cast(campo.get(objeto));
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static void esperarError(Runnable operacion) {
        try {
            operacion.run();
        } catch (IllegalArgumentException | IllegalStateException esperado) {
            return;
        }
        throw new AssertionError("La operación debía rechazar los datos o el estado");
    }

    private static void comprobar(boolean condicion, String mensaje) {
        if (!condicion) throw new AssertionError(mensaje);
    }
}
