package app;

import java.util.ArrayList;
import model.*;
/**
 * Clase principal del sistema SpeedFast.
 * Demuestra el uso de las diferentes clases de pedidos:
 * comida, express y encomienda.
 * @author Carlos Palma
 */
public class Main {
    /**
     * Método principal que crea instancias de los 3 diferentes tipos de pedidos
     */
    public static void main(String[] args) {
    
        var pedidoComida = new PedidoComida("Los arrayanes 568", 3, "Pizza Hawaiana");
        var pedidoExpress = new PedidoExpress("coloso 1584", 6, 5);
        var pedidoExpress2 = new PedidoExpress("Flanders 7683", 5, 3);
        var pedidoEncomienda = new PedidoEncomienda("Salar de Atacama 7958", 10, "Paquete de Libros");
         
        var pedidos = new ArrayList<Pedido>();
        pedidos.add(pedidoComida);
        pedidos.add(pedidoExpress);
        pedidos.add(pedidoExpress2);
        pedidos.add(pedidoEncomienda);

        for (var pedido : pedidos) {
            pedido.mostrarResumen();
            pedido.calcularTiempoEntrega();
        }
    }
}
