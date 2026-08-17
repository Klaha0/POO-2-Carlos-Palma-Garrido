import model.*;
import java.util.ArrayList;

/**
 * Clase principal del sistema SpeedFast.
 * Demuestra el uso de las diferentes clases de pedidos:
 * comida, express y encomienda.
 * @author Carlos Palma
 */
public class Main {
    /**
     * Método principal que crea instancias de los 3 diferentes tipos de pedidos
     * y asigna repartidores para procesarlos
     */
    public static void main(String[] args) {
    var pedidoComida = new PedidoComida(
            "Los arrayanes 568",
            "Super promo 100 piezas de sushi"
            );
      var listaProductos = new ArrayList<String>();
      listaProductos.add("1 Bandeja de pollo");
      listaProductos.add("2 Kg Arroz Tucapel grado 2");
      listaProductos.add("1 Kg azúcar Iansa");
      listaProductos.add("1 lt aceite Belmont");
    var pedidoExpress = new PedidoExpress(
      "coloso 1584",
      true,
            listaProductos
    );

    var pedidoEncomienda = new PedidoEncomienda(
            "Salar de Atacama 7958",
            true,
            "Documentos"
    );

    System.out.println(pedidoComida.AsignarRepartidor("Felipe González"));
    System.out.println(pedidoExpress.AsignarRepartidor("Max Verstappen"));
    System.out.println(pedidoEncomienda.AsignarRepartidor("Daniel Cardoso"));
    }
}
