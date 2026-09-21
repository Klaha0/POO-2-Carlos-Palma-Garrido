package vista;

import javax.swing.*;
import controlador.PedidoControlador;
import modelo.Pedido;
import modelo.TipoPedido;

public class VentanaRegistroPedido extends JFrame {
    private JPanel panelPrincipal;
    private JLabel lblRegistrarPedido;
    private JLabel lblTitulo;
    private JLabel lblId;
    private JLabel lblDireccion;
    private JTextField txtId;
    private JTextField txtDireccion;
    private JButton btnSalir;
    private JButton btnGuardar;
    private JLabel lblTipo;
    private JComboBox<TipoPedido> cmbTipoPedido;
    private JLabel lblUrgenciaValor;
    private final PedidoControlador controlador;

    public VentanaRegistroPedido(PedidoControlador controlador) {
        super("SpeedFast - Registrar pedido");
        this.controlador = controlador;
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cmbTipoPedido.setModel(new DefaultComboBoxModel<>(TipoPedido.values()));
        cmbTipoPedido.setSelectedIndex(-1);
        cmbTipoPedido.addActionListener(e -> actualizarUrgencia());
        actualizarUrgencia();
        txtId.setEnabled(true);
        txtId.setEditable(false);
        txtId.setText("Automático al guardar");
        txtId.setToolTipText("El sistema genera un ID único para cada pedido.");
        btnGuardar.addActionListener(e -> guardarPedido());
        btnSalir.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnGuardar);
        pack();
        setMinimumSize(new java.awt.Dimension(400, 350));
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void guardarPedido() {
        try {
            Pedido pedido = controlador.registrarPedido(txtDireccion.getText(),
                    (TipoPedido) cmbTipoPedido.getSelectedItem());
            txtId.setText(String.valueOf(pedido.getId()));
            JOptionPane.showMessageDialog(this, "Pedido #" + pedido.getId() + " registrado correctamente.",
                    "Pedido guardado", JOptionPane.INFORMATION_MESSAGE);
            txtDireccion.setText("");
            cmbTipoPedido.setSelectedIndex(-1);
            txtId.setText("Automático al guardar");
            txtDireccion.requestFocusInWindow();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Revisa los datos",
                    JOptionPane.WARNING_MESSAGE);
            txtDireccion.requestFocusInWindow();
        }
    }

    private void actualizarUrgencia() {
        TipoPedido tipo = (TipoPedido) cmbTipoPedido.getSelectedItem();
        lblUrgenciaValor.setText(tipo == null ? "Selecciona un tipo de pedido"
                : tipo.getNivelUrgencia() + " (automática)");
    }
}
