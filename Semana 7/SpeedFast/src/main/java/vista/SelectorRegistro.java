package vista;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;

/** Muestra nombres legibles, conservando el ID que necesita el DAO. */
class SelectorRegistro extends JComboBox<Integer> {
    private Map<Integer, String> etiquetas = new LinkedHashMap<>();

    SelectorRegistro() {
        setPrototypeDisplayValue(100000);
        setRenderer((lista, valor, indice, seleccionado, foco) -> {
            JLabel etiqueta = (JLabel) new DefaultListCellRenderer()
                    .getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
            etiqueta.setText(describir(valor));
            return etiqueta;
        });
        setPreferredSize(new java.awt.Dimension(270, 28));
    }

    void cargar(Map<Integer, String> opciones) {
        Object anterior = getSelectedItem();
        etiquetas = new LinkedHashMap<>(opciones);
        setModel(new DefaultComboBoxModel<>(opciones.keySet().toArray(Integer[]::new)));
        if (opciones.containsKey(anterior)) setSelectedItem(anterior);
    }

    String describir(Object id) {
        if (id == null) return "Selecciona un registro";
        return id + " — " + etiquetas.getOrDefault(id, "No disponible");
    }

    void instalarEn(JTable tabla, int columna) {
        tabla.getColumnModel().getColumn(columna).setCellEditor(new DefaultCellEditor(this));
        tabla.getColumnModel().getColumn(columna).setCellRenderer(new DefaultTableCellRenderer() {
            @Override protected void setValue(Object valor) { setText(describir(valor)); }
        });
    }
}
