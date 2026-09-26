package vista;

import controlador.RepartidorControlador;
import modelo.Repartidor;
import java.util.List;
import javax.swing.*;

public class VentanaRepartidores extends VentanaCrud {
    private final RepartidorControlador controlador;
    private final JTextField txtNombre = new JTextField(25);

    public VentanaRepartidores(RepartidorControlador controlador) {
        super("Repartidores", new String[]{"ID", "Nombre"},
                new Class<?>[]{Integer.class, String.class}, new boolean[]{false, true});
        this.controlador = controlador;
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        JButton registrar = new JButton("Registrar repartidor");
        formulario.add(registrar);
        registrar.addActionListener(e -> registrar());
        txtNombre.addActionListener(e -> registrar());
        tabla.getColumnModel().getColumn(0).setMaxWidth(80);
    }

    private void registrar() {
        if (!terminarEdicion()) return;
        try {
            String nombre = textoObligatorio(txtNombre.getText(), "Nombre", 100);
            comprobarGuardado(controlador.registrarRepartidor(new Repartidor(0, nombre)));
            txtNombre.setText("");
            refrescar();
            txtNombre.requestFocusInWindow();
        } catch (IllegalArgumentException | IllegalStateException ex) { mostrarError(ex.getMessage()); }
    }

    @Override protected List<Object[]> consultarFilas() {
        List<Repartidor> repartidores = controlador.getRepartidores();
        if (repartidores == null) throw new IllegalStateException("Revisa la conexión a la base de datos.");
        return repartidores.stream().map(r -> new Object[]{r.getId(), r.getNombre()}).toList();
    }

    @Override protected Object[] guardarCambios(Object[] fila) {
        String nombre = textoObligatorio(fila[1], "Nombre", 100);
        comprobarGuardado(controlador.actualizarRepartidor(new Repartidor((Integer) fila[0], nombre)));
        return new Object[]{fila[0], nombre};
    }

    @Override protected boolean eliminarRegistro(int id) { return controlador.eliminarRepartidor(id); }
}
