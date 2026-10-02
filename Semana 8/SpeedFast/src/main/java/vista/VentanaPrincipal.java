package vista;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import controlador.EntregaControlador;
import controlador.PedidoControlador;
import controlador.RepartidorControlador;

public class VentanaPrincipal extends JFrame {
    private JPanel panelPrincipal;
    private JLabel lblTitulo;
    private JButton registrarPedidoButton;
    private JButton listarPedidosButton;
    private JButton reiniciarPedidosButton;
    private JButton btnSalir;
    private final PedidoControlador controlador = new PedidoControlador();
    private VentanaRegistroPedido registro;
    private VentanaListaPedidos listado;
    private VentanaRepartidores ventanaRepartidores;
    private VentanaEntregas ventanaEntregas;
    private final RepartidorControlador repartidores = new RepartidorControlador();
    private final EntregaControlador entregas = new EntregaControlador();
    private final PedidoControlador pedidos = new PedidoControlador();

    public VentanaPrincipal() {
        super("SpeedFast - Gestión de pedidos");
        inicializarComponentes();
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(440, 500);
        setResizable(false);
        setLocationRelativeTo(null);
        registrarPedidoButton.addActionListener(e -> abrirRegistro());
        listarPedidosButton.addActionListener(e -> abrirListado());
        reiniciarPedidosButton.addActionListener(e -> reiniciarPedidos());
        btnSalir.addActionListener(e -> dispose());
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (registro != null) registro.dispose();
                if (listado != null) listado.dispose();
                if (ventanaRepartidores != null) ventanaRepartidores.dispose();
                if (ventanaEntregas != null) ventanaEntregas.dispose();
            }
        });
    }

    private void inicializarComponentes() {
        panelPrincipal = new JPanel(new java.awt.BorderLayout(12, 16));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JPanel cabecera = new JPanel(new java.awt.GridLayout(2, 1, 0, 8));
        lblTitulo = new JLabel("Speed Fast", SwingConstants.CENTER);
        lblTitulo.setFont(lblTitulo.getFont().deriveFont(java.awt.Font.BOLD, 18f));
        cabecera.add(lblTitulo);
        cabecera.add(new JLabel("Sistema de control de pedidos", SwingConstants.CENTER));
        panelPrincipal.add(cabecera, java.awt.BorderLayout.NORTH);
        JPanel acciones = new JPanel(new java.awt.GridLayout(6, 1, 0, 10));
        registrarPedidoButton = new JButton("Registrar Pedido");
        listarPedidosButton = new JButton("Gestionar Pedidos");
        reiniciarPedidosButton = new JButton("Reiniciar Pedidos");
        btnSalir = new JButton("Salir");
        acciones.add(registrarPedidoButton);
        acciones.add(listarPedidosButton);
        JButton btnRepartidores = new JButton("Gestionar Repartidores");
        JButton btnEntregas = new JButton("Gestionar Entregas");
        btnRepartidores.addActionListener(e -> abrirRepartidores());
        btnEntregas.addActionListener(e -> abrirEntregas());
        acciones.add(btnRepartidores);
        acciones.add(btnEntregas);
        acciones.add(reiniciarPedidosButton);
        acciones.add(btnSalir);
        panelPrincipal.add(acciones, java.awt.BorderLayout.CENTER);
    }

    private void abrirRegistro() {
        if (registro == null || !registro.isDisplayable()) {
            registro = new VentanaRegistroPedido(controlador, () -> {
                if (listado != null && listado.isDisplayable()) listado.refrescar();
                refrescarEntregas();
            });
            registro.setLocationRelativeTo(this);
        }
        registro.setVisible(true);
        registro.toFront();
    }

    private void abrirListado() {
        if (listado == null || !listado.isDisplayable()) {
            listado = new VentanaListaPedidos(controlador);
            listado.setAlCambiar(this::refrescarEntregas);
            listado.setRegistrarEntrega(id -> { abrirEntregas(); ventanaEntregas.seleccionarPedido(id); });
            listado.setLocationRelativeTo(this);
        }
        listado.refrescar();
        listado.setVisible(true);
        listado.toFront();
    }

    private void abrirRepartidores() {
        if (ventanaRepartidores == null || !ventanaRepartidores.isDisplayable()) {
            ventanaRepartidores = new VentanaRepartidores(repartidores);
            ventanaRepartidores.setAlCambiar(this::refrescarEntregas);
            ventanaRepartidores.setLocationRelativeTo(this);
        }
        ventanaRepartidores.refrescar();
        ventanaRepartidores.setVisible(true);
        ventanaRepartidores.toFront();
    }

    private void abrirEntregas() {
        if (ventanaEntregas == null || !ventanaEntregas.isDisplayable()) {
            ventanaEntregas = new VentanaEntregas(entregas, controlador, repartidores, () -> {
                if (listado != null && listado.isDisplayable()) listado.refrescar();
            });
            ventanaEntregas.setLocationRelativeTo(this);
        }
        ventanaEntregas.refrescar();
        ventanaEntregas.setVisible(true);
        ventanaEntregas.toFront();
    }

    private void refrescarEntregas() {
        if (ventanaEntregas != null && ventanaEntregas.isDisplayable()) ventanaEntregas.refrescar();
    }

    /**
     * Reinicia entregas y estados conjuntamente y sincroniza las ventanas abiertas.
     */
    private void reiniciarPedidos(){
        try {
            if(JOptionPane.showConfirmDialog(this, "Esta acción eliminará todas las entregas, y reiniciará los pedidos.\n¿Está seguro de realizar esta acción?",
                    "Confirmar reinicio", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) != JOptionPane.YES_OPTION){
                return;
            }
            if (!pedidos.reiniciarPedidos()) {
                JOptionPane.showMessageDialog(this, "No se pudieron reiniciar los pedidos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (listado != null && listado.isDisplayable()) listado.refrescar();
            refrescarEntregas();
            JOptionPane.showMessageDialog(this, "ENTREGAS eliminadas\nEstado PEDIDOS = PENDIENTE",
                    "Reiniciado", JOptionPane.INFORMATION_MESSAGE);
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
