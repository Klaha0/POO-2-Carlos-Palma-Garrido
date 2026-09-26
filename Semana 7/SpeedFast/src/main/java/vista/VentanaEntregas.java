package vista;

import controlador.*;
import modelo.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class VentanaEntregas extends VentanaCrud {
    private final EntregaControlador controlador;
    private final PedidoControlador pedidos;
    private final RepartidorControlador repartidores;
    private final Runnable alRegistrar;
    private final SelectorRegistro cmbPedido = new SelectorRegistro();
    private final SelectorRegistro cmbRepartidor = new SelectorRegistro();
    private final SelectorRegistro editorPedido = new SelectorRegistro();
    private final SelectorRegistro editorRepartidor = new SelectorRegistro();
    private Map<Integer, String> pedidosDisponibles = new LinkedHashMap<>();
    private Map<Integer, String> todosLosPedidos = new LinkedHashMap<>();
    private boolean opcionesCargadas;
    private final JButton registrar = new JButton("Registrar entrega");

    public VentanaEntregas(EntregaControlador controlador, PedidoControlador pedidos,
                          RepartidorControlador repartidores) {
        this(controlador, pedidos, repartidores, () -> { });
    }

    public VentanaEntregas(EntregaControlador controlador, PedidoControlador pedidos,
                          RepartidorControlador repartidores, Runnable alRegistrar) {
        super("Entregas", new String[]{"ID", "Pedido", "Repartidor", "Fecha", "Hora"},
                new Class<?>[]{Integer.class, Integer.class, Integer.class, String.class, String.class},
                new boolean[]{false, true, true, false, false});
        this.controlador = controlador;
        this.pedidos = pedidos;
        this.repartidores = repartidores;
        this.alRegistrar = alRegistrar;
        formulario.setLayout(new java.awt.GridLayout(3, 1, 0, 6));
        JPanel selecciones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        selecciones.add(new JLabel("Pedido:"));
        selecciones.add(cmbPedido);
        selecciones.add(new JLabel("Repartidor:"));
        selecciones.add(cmbRepartidor);
        formulario.add(selecciones);
        JPanel acciones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        acciones.add(registrar);
        JButton actualizar = new JButton("Actualizar opciones");
        acciones.add(actualizar);
        formulario.add(acciones);
        formulario.add(new JLabel("Fecha y hora automáticas al registrar o modificar una entrega."));
        registrar.setEnabled(false);
        cmbPedido.addActionListener(e -> actualizarBotonRegistrar());
        cmbRepartidor.addActionListener(e -> actualizarBotonRegistrar());
        registrar.addActionListener(e -> registrar());
        actualizar.addActionListener(e -> refrescar());
        editorPedido.instalarEn(tabla, 1);
        tabla.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(new SelectorRegistro()) {
            @Override public java.awt.Component getTableCellEditorComponent(JTable tabla, Object valor,
                    boolean seleccionado, int fila, int columna) {
                Map<Integer, String> opciones = new LinkedHashMap<>(pedidosDisponibles);
                int idActual = (Integer) valor;
                opciones.put(idActual, todosLosPedidos.getOrDefault(idActual, "Pedido actual"));
                ((SelectorRegistro) getComponent()).cargar(opciones);
                return super.getTableCellEditorComponent(tabla, valor, seleccionado, fila, columna);
            }
        });
        editorRepartidor.instalarEn(tabla, 2);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(220);
        setSize(1000, 560);
        setMinimumSize(new java.awt.Dimension(960, 480));
    }

    @Override protected List<Object[]> consultarFilas() {
        opcionesCargadas = false;
        actualizarBotonRegistrar();
        List<Pedido> listaPedidos = pedidos.getPedidos();
        List<Repartidor> listaRepartidores = repartidores.getRepartidores();
        List<Entrega> entregas = controlador.getEntregas();
        if (listaPedidos == null || listaRepartidores == null || entregas == null) {
            registrar.setEnabled(false);
            throw new IllegalStateException("No se pudieron cargar las entregas y sus opciones. Revisa la conexión.");
        }
        Map<Integer, String> opcionesPedidos = new LinkedHashMap<>();
        listaPedidos.forEach(p -> opcionesPedidos.put(p.getId(), p.getDireccion()));
        Map<Integer, String> opcionesRepartidores = new LinkedHashMap<>();
        listaRepartidores.forEach(r -> opcionesRepartidores.put(r.getId(), r.getNombre()));
        todosLosPedidos = opcionesPedidos;
        pedidosDisponibles = new LinkedHashMap<>();
        for (Pedido pedido : listaPedidos) {
            if (controlador.puedeRegistrarEntrega(pedido, entregas)) {
                pedidosDisponibles.put(pedido.getId(), pedido.getDireccion());
            }
        }
        Object seleccionado = cmbPedido.getSelectedItem();
        cmbPedido.cargar(pedidosDisponibles);
        // No elegimos otro pedido automáticamente después de guardar: evita entregas por doble clic.
        if (!pedidosDisponibles.containsKey(seleccionado)) cmbPedido.setSelectedIndex(-1);
        editorPedido.cargar(opcionesPedidos);
        cmbRepartidor.cargar(opcionesRepartidores);
        editorRepartidor.cargar(opcionesRepartidores);
        opcionesCargadas = true;
        actualizarBotonRegistrar();
        return entregas.stream().map(this::filaEntrega).toList();
    }

    private void actualizarBotonRegistrar() {
        boolean disponibles = opcionesCargadas && cmbPedido.getSelectedItem() != null
                && cmbRepartidor.getSelectedItem() != null;
        registrar.setEnabled(disponibles);
        registrar.setToolTipText(disponibles ? "Registrar la entrega seleccionada"
                : "Selecciona un pedido sin entregar y un repartidor. Actualiza las opciones si es necesario.");
    }

    private void registrar() {
        if (!terminarEdicion()) return;
        registrar.setEnabled(false);
        try {
            if (cmbPedido.getSelectedItem() == null || cmbRepartidor.getSelectedItem() == null) {
                throw new IllegalArgumentException("Selecciona un pedido y un repartidor.");
            }
            Entrega entrega = new Entrega(0, (Integer) cmbPedido.getSelectedItem(),
                    (Integer) cmbRepartidor.getSelectedItem(), null, null);
            comprobarGuardado(controlador.registrarEntrega(entrega));
            alRegistrar.run();
            refrescar();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            refrescar();
            mostrarError(ex.getMessage());
        } finally {
            actualizarBotonRegistrar();
        }
    }

    private Object[] filaEntrega(Entrega entrega) {
        return new Object[]{entrega.getId(), entrega.getIdPedido(), entrega.getIdRepartidor(),
                entrega.getFecha(), entrega.getHora()};
    }

    @Override protected Object[] guardarCambios(Object[] fila) {
        if (!(fila[1] instanceof Integer) || !(fila[2] instanceof Integer)) {
            throw new IllegalArgumentException("Selecciona un pedido y un repartidor válidos.");
        }
        Entrega entrega = new Entrega((Integer) fila[0], (Integer) fila[1], (Integer) fila[2], null, null);
        comprobarGuardado(controlador.actualizarEntrega(entrega));
        // Actualizamos las opciones una vez que JTable haya retirado el editor de la celda.
        SwingUtilities.invokeLater(() -> { alRegistrar.run(); refrescar(); });
        // El DAO conserva DATE(NOW()) y TIME(NOW()); releemos esos valores del servidor.
        Entrega guardada = controlador.buscarPorId(entrega.getId());
        if (guardada != null) return filaEntrega(guardada);
        mostrarError("El cambio se guardó, pero no se pudo consultar la nueva fecha y hora. Pulsa Refrescar.");
        return new Object[]{fila[0], fila[1], fila[2], "Pulsa Refrescar", "Pulsa Refrescar"};
    }

    @Override protected boolean eliminarRegistro(int id) { return controlador.eliminarEntrega(id); }
}
