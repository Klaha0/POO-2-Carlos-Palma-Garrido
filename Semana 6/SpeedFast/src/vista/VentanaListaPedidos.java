package vista;

import controlador.PedidoControlador;
import modelo.EstadoPedido;
import modelo.Pedido;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class VentanaListaPedidos extends JFrame {
    private JPanel panelPrincipal;
    private JTable tablaPedidos;
    private JButton btnRefrescar;
    private JButton btnReset;
    private JButton btnCerrar;
    private JLabel lblResumen;
    private final PedidoControlador controlador;
    private final Runnable observador = this::refrescar;
    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new String[]{"ID", "Dirección", "Tipo", "Urgencia", "Estado", "Repartidor"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int columna) {
            return columna == 0 ? Integer.class : String.class;
        }
    };

    public VentanaListaPedidos(PedidoControlador controlador) {
        super("SpeedFast - Listado de pedidos");
        this.controlador = controlador;
        setContentPane(panelPrincipal);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        tablaPedidos.setModel(modeloTabla);
        tablaPedidos.setAutoCreateRowSorter(true);
        tablaPedidos.setRowHeight(26);
        tablaPedidos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPedidos.getColumnModel().getColumn(0).setPreferredWidth(45);
        tablaPedidos.getColumnModel().getColumn(1).setPreferredWidth(270);
        btnRefrescar.addActionListener(e -> refrescar());
        btnReset.addActionListener(e -> controlador.resetearPedidos());
        btnCerrar.addActionListener(e -> dispose());
        controlador.agregarObservador(observador);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                controlador.quitarObservador(observador);
            }
        });
        setSize(920, 430);
        setMinimumSize(new java.awt.Dimension(760, 320));
        setLocationRelativeTo(null);
        refrescar();
    }

    private void refrescar() {
        List<Pedido> pedidos = controlador.getPedidos();
        btnReset.setEnabled(!controlador.isEntregando() && !pedidos.isEmpty());
        modeloTabla.setRowCount(0);
        int pendientes = 0;
        int enReparto = 0;
        int entregados = 0;
        for (Pedido pedido : pedidos) {
            EstadoPedido estado = pedido.getEstado();
            modeloTabla.addRow(new Object[]{pedido.getId(), pedido.getDireccionEntrega(),
                    pedido.getTipo().toString(), pedido.getNivelUrgencia().toString(),
                    estado.toString(), pedido.getNombreRepartidor()});
            switch (estado) {
                case PENDIENTE -> pendientes++;
                case EN_REPARTO -> enReparto++;
                case ENTREGADO -> entregados++;
            }
        }
        lblResumen.setText("Total: " + pedidos.size() + "   |   Pendientes: " + pendientes
                + "   |   En reparto: " + enReparto + "   |   Entregados: " + entregados);
    }
}
