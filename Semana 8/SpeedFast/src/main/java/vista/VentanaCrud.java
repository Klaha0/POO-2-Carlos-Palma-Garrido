package vista;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/** Elementos comunes de los listados: filtro, edición, eliminación y mensajes. */
public abstract class VentanaCrud extends JFrame {
    private Runnable alCambiar = () -> { };
    /**
     * Configura la acción que sincroniza otras ventanas después de modificar registros.
     *
     * @param accion callback de actualización de las ventanas relacionadas.
     */
    public void setAlCambiar(Runnable accion) { alCambiar = accion; }
    /**
     * Programa la sincronización cuando termine la operación actual de Swing.
     */
    protected void notificarCambio() { SwingUtilities.invokeLater(alCambiar); }
    protected final JTable tabla;
    protected final DefaultTableModel modeloTabla;
    protected final JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
    protected final JLabel lblEstado = new JLabel("Pulsa Refrescar para consultar los registros.");
    protected int columnaBusqueda() { return 0; }
    protected final JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
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
                    notificarCambio();
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
        mensajes.add(new JLabel("Doble clic para editar. Enter guardar; Esc cancela."));
        mensajes.add(lblEstado);
        pie.add(mensajes, BorderLayout.CENTER);
        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton eliminar = new JButton("Eliminar selección");
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
        //setSize(600, 500);
        //setMinimumSize(new Dimension(600, 380));
        setLocationRelativeTo(null);
    }

    /**
     * Consulta y convierte los registros en filas para la tabla.
     *
     * @return las filas que se mostrarán en el listado.
     */
    protected abstract List<Object[]> consultarFilas();
    /**
     * Valida y persiste una fila antes de modificar su representación en la tabla.
     *
     * @param fila datos con el cambio solicitado.
     * @return los datos aceptados que se mostrarán en la tabla.
     */
    protected abstract Object[] guardarCambios(Object[] fila);
    /**
     * Ejecuta la eliminación específica de la entidad.
     *
     * @param id identificador seleccionado.
     * @return true si se eliminó el registro.
     */
    protected abstract boolean eliminarRegistro(int id);

    /**
     * Recarga el listado desde MySQL; conserva las filas visibles si la consulta falla.
     */
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

    /**
     * Finaliza la edición de la celda antes de consultar o modificar otros registros.
     *
     * @return true si no hay edición pendiente o se guardó correctamente.
     */
    protected boolean terminarEdicion() {
        edicionValida = true;
        return !tabla.isEditing() || (tabla.getCellEditor().stopCellEditing() && edicionValida);
    }

    /**
     * Filtra las filas cargadas por un ID positivo en la columna de búsqueda seleccionada.
     */
    protected void filtrar() {
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
                        return fila.getValue(columnaBusqueda()).equals(id);
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

    /**
     * Valida la selección, solicita confirmación y retira la fila solo si se eliminó en MySQL.
     */
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
            notificarCambio();
            lblEstado.setText("Registro #" + id + " eliminado.");
        } catch (IllegalArgumentException | IllegalStateException ex) {
            mostrarError(ex.getMessage());
        }
    }

    /**
     * Solicita confirmación antes de eliminar un registro.
     *
     * @param id identificador seleccionado.
     * @return true únicamente si el usuario acepta.
     */
    protected boolean confirmarEliminacion(int id) {
        return JOptionPane.showConfirmDialog(this, "¿Eliminar el registro #" + id + "? Esta acción no se puede deshacer.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    /**
     * Muestra el aviso cuando Swing haya retirado el editor de la celda.
     *
     * @param mensaje detalle de validación o persistencia que se mostrará.
     */
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

    /**
     * Valida y recorta los espacios de un campo obligatorio.
     *
     * @param valor contenido del campo.
     * @param nombre nombre que se mostrará en el aviso.
     * @param maximo longitud máxima admitida.
     * @return el texto validado sin espacios en los extremos.
     */
    protected String textoObligatorio(Object valor, String nombre, int maximo) {
        String texto = valor == null ? "" : valor.toString().trim();
        if (texto.isEmpty() || texto.length() > maximo) {
            throw new IllegalArgumentException(nombre + ": ingresa entre 1 y " + maximo + " caracteres.");
        }
        return texto;
    }
}
