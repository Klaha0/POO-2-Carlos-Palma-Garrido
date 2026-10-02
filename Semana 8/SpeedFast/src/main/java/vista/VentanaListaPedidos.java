package vista;

import controlador.PedidoControlador;
import modelo.*;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaListaPedidos extends VentanaCrud {
    private final PedidoControlador controlador;
    private VentanaRegistroPedido registro;
    private java.util.function.IntConsumer registrarEntrega;

    /**
     * Configura la apertura de entregas cuando se solicita marcar un pedido ENTREGADO.
     *
     * @param accion callback que recibe el ID del pedido.
     */
    public void setRegistrarEntrega(java.util.function.IntConsumer accion) { registrarEntrega = accion; }

    public VentanaListaPedidos(PedidoControlador controlador) {
        super("Pedidos", new String[]{"ID", "Dirección", "Tipo", "Urgencia", "Estado"},
                new Class<?>[]{Integer.class, String.class, TipoPedido.class, String.class, EstadoPedido.class},
                new boolean[]{false, true, true, false, true});
        this.controlador = controlador;
        setSize(670,450);
        setMinimumSize(new Dimension(670, 450));
        JButton nuevo = new JButton("Registrar pedido");
        formulario.add(nuevo);
        formulario.add(new JLabel("ID y urgencia se asignan automáticamente."));
        nuevo.addActionListener(e -> abrirRegistro());
        tabla.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JComboBox<>(TipoPedido.values())));
        tabla.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(new JComboBox<>(EstadoPedido.values())));
        tabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(30);
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosed(WindowEvent e) {
                if (registro != null) registro.dispose();
            }
        });
    }

    private void abrirRegistro() {
        if (!terminarEdicion()) return;
        if (registro == null || !registro.isDisplayable()) {
            registro = new VentanaRegistroPedido(controlador, () -> { refrescar(); notificarCambio(); });
            registro.setLocationRelativeTo(this);
        }
        registro.setVisible(true);
        registro.toFront();
    }

    @Override protected List<Object[]> consultarFilas() {
        List<Pedido> pedidos = controlador.getPedidos();
        if (pedidos == null) throw new IllegalStateException("Revisa la conexión a la base de datos.");
        return pedidos.stream().map(this::filaPedido).toList();
    }

    private Object[] filaPedido(Pedido pedido) {
        return new Object[]{pedido.getId(), pedido.getDireccion(), pedido.getTipo(),
                pedido.getNivelUrgencia().toString(), pedido.getEstado()};
    }

    /**
     * Guarda los cambios o solicita una entrega conservando el estado anterior hasta registrarla.
     *
     * @param fila pedido con los valores editados.
     * @return la fila correspondiente al estado persistido.
     */
    @Override protected Object[] guardarCambios(Object[] fila) {
        String direccion = textoObligatorio(fila[1], "Dirección", 100);
        if (!(fila[2] instanceof TipoPedido) || !(fila[4] instanceof EstadoPedido)) {
            throw new IllegalArgumentException("Selecciona un tipo y un estado válidos.");
        }
        Pedido pedido = new Pedido((Integer) fila[0], direccion, (EstadoPedido) fila[4], (TipoPedido) fila[2]);
        // ENTREGADO requiere una entrega persistida; conservamos el estado hasta registrarla.
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            Pedido actual = controlador.buscarPorId(pedido.getId());
            if (actual == null) throw new IllegalStateException("El pedido ya no existe. Refresca el listado.");
            if (actual.getEstado() != EstadoPedido.ENTREGADO) {
                if (registrarEntrega == null) throw new IllegalStateException("Abre la gestión de entregas para completar el pedido.");
                SwingUtilities.invokeLater(() -> registrarEntrega.accept(pedido.getId()));
                return filaPedido(actual);
            }
        }
        comprobarGuardado(controlador.actualizarPedido(pedido));
        return filaPedido(pedido);
    }

    @Override protected boolean eliminarRegistro(int id) { return controlador.eliminarPedido(id); }
}
