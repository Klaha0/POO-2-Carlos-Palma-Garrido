package vista;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controlador.PedidoControlador;
import modelo.EstadoPedido;
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
    private final Runnable alGuardar;

    public VentanaRegistroPedido(PedidoControlador controlador) {
        this(controlador, () -> { });
    }

    public VentanaRegistroPedido(PedidoControlador controlador, Runnable alGuardar) {
        super("SpeedFast - Registrar pedido");
        this.alGuardar = alGuardar;
        this.controlador = controlador;
        inicializarComponentes();
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        cmbTipoPedido.setModel(new DefaultComboBoxModel<>(TipoPedido.values()));
        cmbTipoPedido.setSelectedIndex(-1);
        cmbTipoPedido.addActionListener(e -> actualizarUrgencia());
        actualizarUrgencia();
        txtId.setEnabled(true);
        txtId.setEditable(false);
        txtId.setText("Automático al guardar");
        txtId.setToolTipText("La base de datos genera el ID al guardar.");
        btnGuardar.addActionListener(e -> guardarPedido());
        btnSalir.addActionListener(e -> dispose());
        getRootPane().setDefaultButton(btnGuardar);
        pack();
        setMinimumSize(new java.awt.Dimension(400, 350));
        setResizable(false);
        setLocationRelativeTo(null);
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new java.awt.BorderLayout(12, 16));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));
        JPanel cabecera = new JPanel(new java.awt.GridLayout(2, 1, 0, 8));
        lblTitulo = new JLabel("Speed Fast", SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        lblRegistrarPedido = new JLabel("Registrar Pedido", SwingConstants.CENTER);
        lblRegistrarPedido.setFont(lblRegistrarPedido.getFont().deriveFont(java.awt.Font.BOLD, 14f));
        cabecera.add(lblTitulo);
        cabecera.add(lblRegistrarPedido);
        panelPrincipal.add(cabecera, java.awt.BorderLayout.NORTH);
        JPanel campos = new JPanel(new java.awt.GridBagLayout());
        lblTipo = new JLabel("Tipo de Pedido:");
        lblId = new JLabel("ID:");
        lblDireccion = new JLabel("Dirección:");
        cmbTipoPedido = new JComboBox<>();
        txtId = new JTextField(20);
        txtDireccion = new JTextField(20);
        lblUrgenciaValor = new JLabel();
        JLabel[] etiquetas = {lblTipo, lblId, lblDireccion, new JLabel("Urgencia:")};
        JComponent[] entradas = {cmbTipoPedido, txtId, txtDireccion, lblUrgenciaValor};
        for (int fila = 0; fila < etiquetas.length; fila++) {
            java.awt.GridBagConstraints posicion = new java.awt.GridBagConstraints();
            posicion.gridy = fila;
            posicion.gridx = 0;
            posicion.anchor = java.awt.GridBagConstraints.LINE_END;
            posicion.insets = new java.awt.Insets(6, 0, 6, 10);
            campos.add(etiquetas[fila], posicion);
            posicion.gridx = 1;
            posicion.weightx = 1;
            posicion.fill = java.awt.GridBagConstraints.HORIZONTAL;
            posicion.insets = new java.awt.Insets(6, 0, 6, 0);
            campos.add(entradas[fila], posicion);
            etiquetas[fila].setLabelFor(entradas[fila]);
        }
        panelPrincipal.add(campos, java.awt.BorderLayout.CENTER);
        JPanel acciones = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
        btnGuardar = new JButton("Guardar");
        btnSalir = new JButton("Salir");
        acciones.add(btnGuardar);
        acciones.add(btnSalir);
        panelPrincipal.add(acciones, java.awt.BorderLayout.SOUTH);
    }

    private void guardarPedido() {
        try {
            if (txtDireccion.getText().trim().length() > 150) {
                throw new IllegalArgumentException("La dirección admite hasta 150 caracteres.");
            }
            boolean guardado = controlador.registrarPedido(txtDireccion.getText(),
                    EstadoPedido.PENDIENTE, (TipoPedido) cmbTipoPedido.getSelectedItem());
            if (!guardado) {
                JOptionPane.showMessageDialog(this, "No se pudo guardar el pedido. Revisa la conexión y la tabla pedido.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Pedido registrado correctamente.",
                    "Pedido guardado", JOptionPane.INFORMATION_MESSAGE);
            txtDireccion.setText("");
            cmbTipoPedido.setSelectedIndex(-1);
            txtId.setText("Automático al guardar");
            alGuardar.run();
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
