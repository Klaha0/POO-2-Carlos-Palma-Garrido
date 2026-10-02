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
    private boolean opcionesCargadas;
    private final JComboBox<String> buscarPor = new JComboBox<>(new String[]{"Entrega", "Pedido", "Repartidor"});
    @Override protected int columnaBusqueda() { return buscarPor.getSelectedIndex(); }
    private final JButton registrar = new JButton("Registrar entrega");

    public VentanaEntregas(EntregaControlador controlador, PedidoControlador pedidos,
                          RepartidorControlador repartidores) {
        this(controlador, pedidos, repartidores, () -> { });
    }

    public VentanaEntregas(EntregaControlador controlador, PedidoControlador pedidos,
                          RepartidorControlador repartidores, Runnable alRegistrar) {
        super("Entregas", new String[]{"ID", "Pedido", "Repartidor", "Fecha", "Hora"},
                new Class<?>[]{Integer.class, Integer.class, Integer.class, String.class, String.class},
                new boolean[]{false, false, true, false, false});
        this.controlador = controlador;
        busqueda.add(new JLabel("Buscar por:"), 0);
        busqueda.add(buscarPor, 1);
        buscarPor.addActionListener(e -> filtrar());
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
        formulario.add(new JLabel("Fecha y hora automáticas al registrar; se conservan al editar."));
        registrar.setEnabled(false);
        cmbPedido.addActionListener(e -> actualizarBotonRegistrar());
        cmbRepartidor.addActionListener(e -> actualizarBotonRegistrar());
        registrar.addActionListener(e -> registrar());
        actualizar.addActionListener(e -> refrescar());
        editorPedido.instalarEn(tabla, 1);
        editorRepartidor.instalarEn(tabla, 2);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(280);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(220);
        setSize(800, 560);
        setMinimumSize(new java.awt.Dimension(800, 480));
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

    /**
     * Preselecciona el pedido solicitado desde la lista y exige elegir un repartidor.
     *
     * @param id identificador del pedido disponible para entregar.
     */
    public void seleccionarPedido(int id) {
        if (!pedidosDisponibles.containsKey(id)) {
            mostrarError("El pedido ya no está disponible para registrar una entrega.");
            return;
        }
        cmbPedido.setSelectedItem(id);
        cmbRepartidor.setSelectedIndex(-1);
        cmbRepartidor.requestFocusInWindow();
    }

    private void actualizarBotonRegistrar() {
        boolean disponibles = opcionesCargadas && cmbPedido.getSelectedItem() != null
                && cmbRepartidor.getSelectedItem() != null;
        registrar.setEnabled(disponibles);
        registrar.setToolTipText(disponibles ? "Registrar la entrega seleccionada"
                : "Selecciona un pedido sin entregar y un repartidor. Actualiza las opciones si es necesario.");
    }

    /**
     * Registra la entrega seleccionada y sincroniza los pedidos y las opciones disponibles.
     */
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

    /**
     * Actualiza únicamente el repartidor y vuelve a consultar los datos originales de la entrega.
     *
     * @param fila entrega con el nuevo repartidor seleccionado.
     * @return los datos persistidos que se mostrarán en la tabla.
     */
    @Override protected Object[] guardarCambios(Object[] fila) {
        if (!(fila[1] instanceof Integer) || !(fila[2] instanceof Integer)) {
            throw new IllegalArgumentException("Selecciona un pedido y un repartidor válidos.");
        }
        Entrega entrega = new Entrega((Integer) fila[0], (Integer) fila[1], (Integer) fila[2], null, null);
        comprobarGuardado(controlador.actualizarEntrega(entrega));
        // Actualizamos las opciones una vez que JTable haya retirado el editor de la celda.
        SwingUtilities.invokeLater(() -> { alRegistrar.run(); refrescar(); });
        // Releemos la entrega conservando su fecha y hora originales.
        Entrega guardada = controlador.buscarPorId(entrega.getId());
        if (guardada != null) return filaEntrega(guardada);
        mostrarError("El cambio se guardó, pero no se pudo consultar la entrega actualizada. Pulsa Refrescar.");
        return new Object[]{fila[0], fila[1], fila[2], "Pulsa Refrescar", "Pulsa Refrescar"};
    }

    /**
     * Advierte que eliminar la entrega reiniciará su pedido a PENDIENTE.
     *
     * @param id identificador de la entrega seleccionada.
     * @return true únicamente si el usuario acepta la advertencia.
     */
    @Override protected boolean confirmarEliminacion(int id) {
        return JOptionPane.showConfirmDialog(this,
                "Si elimina este registro el pedido se reiniciar\u00e1 a PENDIENTE",
                "Confirmar eliminaci\u00f3n", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)
                == JOptionPane.YES_OPTION;
    }

    /**
     * Elimina la entrega con su controlador y programa la actualización de tablas y combos.
     *
     * @param id identificador de la entrega.
     * @return true si se eliminó y se reinició el pedido asociado.
     */
    @Override protected boolean eliminarRegistro(int id) {
        boolean eliminado = controlador.eliminarEntrega(id);
        if (eliminado) {
            // Esperamos a que el listado retire la fila antes de recargar las opciones.
            SwingUtilities.invokeLater(() -> { alRegistrar.run(); refrescar(); });
        }
        return eliminado;
    }
}
