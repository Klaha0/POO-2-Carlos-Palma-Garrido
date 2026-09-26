package vista;

import javax.swing.*;
import controlador.PedidoControlador;
import controlador.RepartidorControlador;
import controlador.EntregaControlador;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaPrincipal extends JFrame {
    private JPanel panelPrincipal;
    private JLabel lblTitulo;
    private JButton registrarPedidoButton;
    private JButton listarPedidosButton;
    private JButton btnSalir;
    private final PedidoControlador controlador = new PedidoControlador();
    private VentanaRegistroPedido registro;
    private VentanaListaPedidos listado;
    private VentanaRepartidores ventanaRepartidores;
    private VentanaEntregas ventanaEntregas;
    private final RepartidorControlador repartidores = new RepartidorControlador();
    private final EntregaControlador entregas = new EntregaControlador();

    public VentanaPrincipal() {
        super("SpeedFast - Gestión de pedidos");
        inicializarComponentes();
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(440, 440);
        setResizable(false);
        setLocationRelativeTo(null);
        registrarPedidoButton.addActionListener(e -> abrirRegistro());
        listarPedidosButton.addActionListener(e -> abrirListado());
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
        JPanel acciones = new JPanel(new java.awt.GridLayout(5, 1, 0, 10));
        registrarPedidoButton = new JButton("Registrar Pedido");
        listarPedidosButton = new JButton("Gestionar Pedidos");
        btnSalir = new JButton("Salir");
        acciones.add(registrarPedidoButton);
        acciones.add(listarPedidosButton);
        JButton btnRepartidores = new JButton("Gestionar Repartidores");
        JButton btnEntregas = new JButton("Gestionar Entregas");
        btnRepartidores.addActionListener(e -> abrirRepartidores());
        btnEntregas.addActionListener(e -> abrirEntregas());
        acciones.add(btnRepartidores);
        acciones.add(btnEntregas);
        acciones.add(btnSalir);
        panelPrincipal.add(acciones, java.awt.BorderLayout.CENTER);
    }

    private void abrirRegistro() {
        if (registro == null || !registro.isDisplayable()) {
            registro = new VentanaRegistroPedido(controlador, () -> {
                if (listado != null && listado.isDisplayable()) listado.refrescar();
            });
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
        listado.refrescar();
        listado.setVisible(true);
        listado.toFront();
    }

    private void abrirRepartidores() {
        if (ventanaRepartidores == null || !ventanaRepartidores.isDisplayable()) {
            ventanaRepartidores = new VentanaRepartidores(repartidores);
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
}
