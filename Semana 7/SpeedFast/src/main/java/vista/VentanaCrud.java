package vista;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/** Elementos comunes de los listados: filtro, edición, eliminación y mensajes. */
public abstract class VentanaCrud extends JFrame {
    protected final JTable tabla;
    protected final DefaultTableModel modeloTabla;
    protected final JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    protected final JLabel lblEstado = new JLabel("Pulsa Refrescar para consultar los registros.");
    private final JTextField txtBuscar = new JTextField(8);
    private final TableRowSorter<DefaultTableModel> ordenador;
    private boolean edicionValida = true;

    protected VentanaCrud(String titulo, String[] columnas, Class<?>[] tipos, boolean[] editables) {
        super("SpeedFast - " + titulo);
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return editables[columna]; }
            @Override public Class<?> getColumnClass(int columna) { return tipos[columna]; }

            @Override
            public void setValueAt(Object valor, int fila, int columna) {
                if (!isCellEditable(fila, columna)) return;
                edicionValida = true;
                if (Objects.equals(valor, getValueAt(fila, columna))) return;
                Object[] datos = new Object[getColumnCount()];
                for (int i = 0; i < datos.length; i++) datos[i] = getValueAt(fila, i);
                datos[columna] = valor;
                try {
                    // Solo cambiamos la tabla si la actualización fue aceptada por la BD.
                    Object[] guardados = guardarCambios(datos);
                    for (int i = 0; i < guardados.length; i++) {
                        super.setValueAt(guardados[i], fila, i);
                    }
                    lblEstado.setText("Cambios guardados en el registro #" + datos[0] + ".");
                } catch (IllegalArgumentException | IllegalStateException ex) {
                    edicionValida = false;
                    mostrarError(ex.getMessage());
                }
            }
        };
        tabla = new JTable(modeloTabla);
        tabla.setRowHeight(27);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFillsViewportHeight(true);
        tabla.putClientProperty("terminateEditOnFocusLost", true);
        ordenador = new TableRowSorter<>(modeloTabla);
        tabla.setRowSorter(ordenador);
        tabla.getTableHeader().setReorderingAllowed(false);

        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        JPanel cabecera = new JPanel();
        cabecera.setLayout(new BoxLayout(cabecera, BoxLayout.Y_AXIS));
        JLabel etiqueta = new JLabel(titulo);
        etiqueta.setFont(etiqueta.getFont().deriveFont(Font.BOLD, 18f));
        etiqueta.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.add(etiqueta);
        formulario.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.add(formulario);
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        busqueda.add(new JLabel("Buscar ID:"));
        busqueda.add(txtBuscar);
        JButton buscar = new JButton("Buscar");
        JButton todos = new JButton("Ver todos");
        buscar.addActionListener(e -> filtrar());
        txtBuscar.addActionListener(e -> filtrar());
        todos.addActionListener(e -> { txtBuscar.setText(""); filtrar(); });
        busqueda.add(buscar);
        busqueda.add(todos);
        busqueda.setAlignmentX(Component.LEFT_ALIGNMENT);
        cabecera.add(busqueda);
        panel.add(cabecera, BorderLayout.NORTH);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JPanel pie = new JPanel(new BorderLayout(8, 8));
        JPanel mensajes = new JPanel(new GridLayout(2, 1, 0, 5));
        mensajes.add(new JLabel("Doble clic para editar. Enter guarda el cambio; Esc cancela la edición."));
        mensajes.add(lblEstado);
        pie.add(mensajes, BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton eliminar = new JButton("Eliminar seleccionado");
        JButton refrescar = new JButton("Refrescar");
        JButton cerrar = new JButton("Cerrar");
        eliminar.addActionListener(e -> eliminarSeleccionado());
        refrescar.addActionListener(e -> refrescar());
        cerrar.addActionListener(e -> dispose());
        acciones.add(eliminar);
        acciones.add(refrescar);
        acciones.add(cerrar);
        pie.add(acciones, BorderLayout.SOUTH);
        panel.add(pie, BorderLayout.SOUTH);
        setContentPane(panel);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(960, 500);
        setMinimumSize(new Dimension(880, 380));
        setLocationRelativeTo(null);
    }

    protected abstract List<Object[]> consultarFilas();
    protected abstract Object[] guardarCambios(Object[] fila);
    protected abstract boolean eliminarRegistro(int id);

    public void refrescar() {
        if (!terminarEdicion()) return;
        try {
            List<Object[]> filas = consultarFilas();
            if (filas == null) throw new IllegalStateException("No se pudieron consultar los registros.");
            modeloTabla.setRowCount(0);
            filas.forEach(modeloTabla::addRow);
            mostrarTotal();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarError("No se pudo refrescar el listado. " + ex.getMessage());
        }
    }

    protected boolean terminarEdicion() {
        edicionValida = true;
        return !tabla.isEditing() || (tabla.getCellEditor().stopCellEditing() && edicionValida);
    }

    private void filtrar() {
        if (!terminarEdicion()) return;
        String texto = txtBuscar.getText().trim();
        if (texto.isEmpty()) {
            ordenador.setRowFilter(null);
        } else {
            try {
                int id = Integer.parseInt(texto);
                if (id <= 0) throw new NumberFormatException();
                ordenador.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
                    @Override public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> fila) {
                        return fila.getValue(0).equals(id);
                    }
                });
            } catch (NumberFormatException ex) {
                mostrarError("Ingresa un ID entero mayor que cero.");
                return;
            }
        }
        mostrarTotal();
    }

    private void mostrarTotal() {
        lblEstado.setText("Registros visibles: " + tabla.getRowCount() + " de " + modeloTabla.getRowCount()
                + (tabla.getRowCount() == 0 ? " — Sin resultados." : ""));
    }

    protected void eliminarSeleccionado() {
        if (!terminarEdicion()) return;
        int seleccion = tabla.getSelectedRow();
        if (seleccion < 0) { mostrarError("Selecciona un registro para eliminar."); return; }
        int fila = tabla.convertRowIndexToModel(seleccion);
        int id = (Integer) modeloTabla.getValueAt(fila, 0);
        if (!confirmarEliminacion(id)) return;
        try {
            if (!eliminarRegistro(id)) {
                throw new IllegalStateException("No se pudo eliminar el registro. Revisa la conexión y que aún exista. "
                        + "Si es un pedido o repartidor con entregas asociadas, elimina primero esas entregas.");
            }
            modeloTabla.removeRow(fila);
            lblEstado.setText("Registro #" + id + " eliminado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarError(ex.getMessage());
        }
    }

    protected boolean confirmarEliminacion(int id) {
        return JOptionPane.showConfirmDialog(this, "¿Eliminar el registro #" + id + "? Esta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    protected void mostrarError(String mensaje) {
        lblEstado.setText(mensaje);
        // Esperamos a que JTable retire el editor antes de abrir un diálogo que cambie el foco.
        SwingUtilities.invokeLater(() -> {
            if (isDisplayable()) {
                JOptionPane.showMessageDialog(this, mensaje, "Revisa la operación", JOptionPane.WARNING_MESSAGE);
            }
        });
    }

    protected void comprobarGuardado(boolean guardado) {
        if (!guardado) throw new IllegalStateException("No se pudo guardar. Revisa la conexión y que los registros aún existan.");
    }

    protected String textoObligatorio(Object valor, String nombre, int maximo) {
        String texto = valor == null ? "" : valor.toString().trim();
        if (texto.isEmpty() || texto.length() > maximo) {
            throw new IllegalArgumentException(nombre + ": ingresa entre 1 y " + maximo + " caracteres.");
        }
        return texto;
    }
}
