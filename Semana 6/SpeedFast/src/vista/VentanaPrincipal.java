package vista;

import javax.swing.*;
import controlador.PedidoControlador;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaPrincipal extends JFrame {
    private JPanel panelPrincipal;
    private JLabel lblTitulo;
    private JButton registrarPedidoButton;
    private JButton listarPedidosButton;
    private JButton asignarRepartidorButton;
    private JButton btnSalir;
    private JLabel lblEstado;
    private final PedidoControlador controlador = new PedidoControlador();
    private VentanaRegistroPedido registro;
    private VentanaListaPedidos listado;

    public VentanaPrincipal() {
        super("SpeedFast - Gestión de pedidos");
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(320, 350);
        setResizable(false);
        setLocationRelativeTo(null);
        registrarPedidoButton.addActionListener(e -> abrirRegistro());
        listarPedidosButton.addActionListener(e -> abrirListado());
        asignarRepartidorButton.addActionListener(e -> {
            try {
                controlador.iniciarEntregas();
                abrirListado();
            } catch (IllegalStateException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Iniciar entregas",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });
        btnSalir.addActionListener(e -> dispose());
        controlador.agregarObservador(this::actualizarEstado);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                controlador.cerrar();
                if (registro != null) registro.dispose();
                if (listado != null) listado.dispose();
            }
        });
        actualizarEstado();
    }

    private void abrirRegistro() {
        if (registro == null || !registro.isDisplayable()) {
            registro = new VentanaRegistroPedido(controlador);
            registro.setLocationRelativeTo(this);
        }
        registro.setVisible(true);
        registro.toFront();
    }

    private void abrirListado() {
        if (listado == null || !listado.isDisplayable()) {
            listado = new VentanaListaPedidos(controlador);
            listado.setLocationRelativeTo(this);
        }
        listado.setVisible(true);
        listado.toFront();
    }

    private void actualizarEstado() {
        asignarRepartidorButton.setEnabled(!controlador.isEntregando());
        asignarRepartidorButton.setText(controlador.isEntregando()
                ? "Entregas en curso…" : "Asignar repartidor / Iniciar entrega");
        lblEstado.setText(controlador.getMensaje());
    }
}
