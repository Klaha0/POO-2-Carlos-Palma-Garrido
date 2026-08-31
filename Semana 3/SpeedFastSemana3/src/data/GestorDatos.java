package data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import static java.util.Objects.isNull;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

/**
 * Administra la colección de pedidos y su almacenamiento en archivo.
 * Es el único adicional respecto del requerimiento: permite que los pedidos
 * sobrevivan entre ejecuciones y que el historial acumule entregas anteriores.
 */
public class GestorDatos {
    private static final String DATOS_SPEEDFAST = "resources/DatosSpeedFast.txt";
    private static final ArrayList<Pedido> pedidos = new ArrayList<>();

    /**
     * Lee todos los pedidos guardados y los reconstruye en memoria.
     * @return lista de pedidos cargados
     */
    public ArrayList<Pedido> leerArchivo() {
        pedidos.clear();
        File archivo = new File(DATOS_SPEEDFAST);

        try {
            asegurarArchivo(archivo);
            try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
                String linea;
                while ((linea = lector.readLine()) != null) {
                    cargarPedido(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudieron leer los pedidos: " + e.getMessage());
        }

        return pedidos;
    }

    /**
     * Convierte una línea del archivo en un pedido concreto.
     * @param linea registro delimitado que será interpretado
     */
    private void cargarPedido(String linea) {
        try {
            String[] datos = linea.split(";", -1);
            if (datos.length != 5 && datos.length != 6) {
                System.out.println("Registro ignorado por estar incompleto: " + linea);
                return;
            }

            String tipo = datos[0];
            String direccion = datos[1];
            int distancia = Integer.parseInt(datos[2]);
            String estado = datos[3];

            Pedido pedido = switch (tipo) {
                case "PedidoComida" -> new PedidoComida(direccion, distancia, estado, datos[4]);
                case "PedidoEncomienda" -> new PedidoEncomienda(direccion, distancia, estado, datos[4]);
                case "PedidoExpress" -> new PedidoExpress(direccion, distancia, estado, Integer.parseInt(datos[4]));
                default -> null;
            };

            if (isNull(pedido)) {
                System.out.println("Tipo de pedido desconocido: " + tipo);
                return;
            }
            if (datos.length == 6 && !datos[5].isBlank()) {
                pedido.cargarRepartidor(datos[5]);
            }
            pedidos.add(pedido);
        } catch (NumberFormatException e) {
            System.out.println("Error al interpretar un número en el registro: " + linea);
        }
    }

    /**
     * Agrega un pedido a la colección si aún no está registrado.
     * @param pedido pedido que se incorporará
     */
    public void agregarEntidad(Pedido pedido) {
        if (!pedidos.contains(pedido)) {
            pedidos.add(pedido);
        }
    }

    /**
     * Devuelve todos los pedidos recuperados del archivo a su estado inicial,
     * de modo que la demostración pueda repetirse sin crear pedidos nuevos.
     */
    public void reiniciarEstados() {
        for (Pedido pedido : pedidos) {
            pedido.reiniciarParaDemostracion();
        }
    }

    /**
     * Guarda todos los pedidos en el archivo de datos, sobrescribiendo el
     * contenido previo. Si el archivo no existe, se crea.
     */
    public void guardarTodos() {
        File archivo = new File(DATOS_SPEEDFAST);
        try {
            asegurarArchivo(archivo);
            try (BufferedWriter escritor = new BufferedWriter(new FileWriter(archivo, false))) {
                for (Pedido pedido : pedidos) {
                    escritor.write(pedido.persistir());
                    escritor.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("No se pudieron guardar los pedidos: " + e.getMessage());
        }
    }

    /**
     * Muestra únicamente los pedidos que tienen estado Entregado, incluidos los
     * que quedaron guardados en ejecuciones anteriores.
     */
    public void mostrarHistorial() {
        System.out.println("==== Historial de pedidos entregados ====");
        boolean hayEntregas = false;
        for (Pedido pedido : pedidos) {
            if (pedido.getEstadoPedido().equalsIgnoreCase("Entregado")) {
                System.out.println("- " + pedido.getTipoPedido() + " " + pedido.getIdPedido() +
                        " - entregado por " + pedido.getRepartidorAsignado());
                hayEntregas = true;
            }
        }
        if (!hayEntregas) {
            System.out.println("Aún no hay pedidos entregados.");
        }
    }

    /**
     * Crea la carpeta y el archivo de datos cuando aún no existen.
     * @param archivo archivo que debe quedar disponible
     * @throws IOException si no es posible crear la ruta de almacenamiento
     */
    private void asegurarArchivo(File archivo) throws IOException {
        File carpeta = archivo.getParentFile();
        if (!isNull(carpeta) && !carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("no se pudo crear la carpeta de datos");
        }
        if (!archivo.exists() && !archivo.createNewFile()) {
            throw new IOException("no se pudo crear el archivo de datos");
        }
    }
}
