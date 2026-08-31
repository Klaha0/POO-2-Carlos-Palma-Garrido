package model;

import data.GestorDatos;
import static java.util.Objects.isNull;

/**
 * Clase base abstracta para todos los pedidos de SpeedFast.
 * Define los atributos y comportamientos comunes de la jerarquía e implementa
 * las interfaces Despachable, Cancelable y Rastreable.
 */
public abstract class Pedido implements Despachable, Cancelable, Rastreable {
    private static int asignaId = 10000;

    protected final GestorDatos gestorDatos = new GestorDatos();
    protected final String tipoPedido;
    private final int idPedido;
    protected final String direccionEntrega;
    protected final int distanciaEnKm;
    protected String estadoPedido;
    protected String repartidorAsignado;

    /**
     * Construye un pedido con los datos comunes de la jerarquía.
     * @param tipoPedido tipo concreto del pedido
     * @param direccionEntrega dirección de destino
     * @param distanciaEnKm distancia hasta el destino
     * @param estadoPedido estado inicial del pedido
     */
    protected Pedido(String tipoPedido, String direccionEntrega, int distanciaEnKm, String estadoPedido) {
        this.tipoPedido = tipoPedido;
        this.direccionEntrega = direccionEntrega;
        this.distanciaEnKm = distanciaEnKm;
        this.estadoPedido = estadoPedido;
        this.idPedido = asignaId++;
    }

    /** @return identificador único del pedido. */
    public int getIdPedido() {
        return idPedido;
    }

    /** @return estado actual del pedido. */
    public String getEstadoPedido() {
        return estadoPedido;
    }

    /** @return repartidor a cargo, o null si todavía no tiene. */
    public String getRepartidorAsignado() {
        return repartidorAsignado;
    }

    /** @return tipo al que pertenece el pedido. */
    public String getTipoPedido() {
        return tipoPedido;
    }

    /**
     * Restaura el repartidor leído desde el archivo.
     * @param nombreRepartidor nombre del repartidor guardado
     */
    public void cargarRepartidor(String nombreRepartidor) {
        this.repartidorAsignado = nombreRepartidor;
    }

    /**
     * Devuelve el pedido a su estado inicial para poder repetir la demostración
     * con los pedidos que se recuperaron del archivo.
     */
    public void reiniciarParaDemostracion() {
        this.estadoPedido = "Creado";
        this.repartidorAsignado = null;
    }

    /**
     * Agrega este pedido a la colección administrada por GestorDatos.
     */
    protected void agregarAPedidos() {
        gestorDatos.agregarEntidad(this);
    }

    /**
     * Muestra el resumen del pedido. El contenido lo aporta cada subclase
     * mediante su propio toString(), de modo que el mismo llamado imprime
     * información distinta según el tipo de pedido.
     */
    public void mostrarResumen() {
        System.out.println(this);
    }

    /**
     * Reserva el pedido cuando aún se encuentra recién creado.
     */
    public void reservar() {
        if (this.estadoPedido.equalsIgnoreCase("Creado")) {
            this.estadoPedido = "Reservado";
            System.out.println("Pedido " + this.idPedido + " reservado correctamente.\n");
        } else {
            System.out.println("El pedido " + this.idPedido + " no se puede reservar en su estado actual.\n");
        }
    }

    /**
     * Valida el nombre recibido en una asignación manual y avisa cuando no sirve.
     * @param nombreRepartidor nombre entregado por el usuario
     * @return true si el nombre puede usarse para asignar
     */
    protected boolean esRepartidorValido(String nombreRepartidor) {
        if (isNull(nombreRepartidor) || nombreRepartidor.isBlank()) {
            System.out.println("No se indicó un repartidor válido para el pedido " + this.idPedido + ".\n");
            return false;
        }
        return true;
    }

    /**
     * @return resumen específico de la subclase.
     */
    @Override
    public abstract String toString();

    /**
     * Asigna automáticamente un repartidor según la regla de cada tipo de pedido.
     */
    public abstract void asignarRepartidor();

    /**
     * Asigna manualmente el repartidor indicado por el usuario.
     * Sobrecarga del método anterior, implementada por cada tipo de pedido.
     * @param nombreRepartidor nombre del repartidor
     */
    public abstract void asignarRepartidor(String nombreRepartidor);

    /**
     * Calcula y muestra el tiempo estimado de entrega con la fórmula propia
     * de cada tipo de pedido.
     */
    public abstract void calcularTiempoEntrega();

    /**
     * @return representación en cadena de texto del pedido para persistencia.
     */
    public abstract String persistir();

    // Implementación de las interfaces Despachable, Cancelable y Rastreable

    // Despacha el pedido cuando ya tiene repartidor y todavía no sale a la calle.
    @Override
    public void despachar() {
        if (isNull(this.repartidorAsignado) || this.repartidorAsignado.isBlank()) {
            System.out.println("No se puede despachar el pedido sin repartidor asignado.\n");
            return;
        }

        if (this.estadoPedido.equalsIgnoreCase("Creado") || this.estadoPedido.equalsIgnoreCase("Reservado")) {
            this.estadoPedido = "Despachado";
            System.out.println("Pedido " + this.idPedido + " despachado correctamente.");
            calcularTiempoEntrega();
        } else {
            System.out.println("El pedido " + this.idPedido + " no se puede despachar en su estado actual.\n");
        }
    }

    // Marca como entregado un pedido previamente despachado.
    @Override
    public void entregar() {
        if (this.estadoPedido.equalsIgnoreCase("Despachado")) {
            this.estadoPedido = "Entregado";
            System.out.println("Pedido " + this.idPedido + " entregado correctamente.\n");
        } else {
            System.out.println("El pedido " + this.idPedido + " debe estar despachado para ser entregado.\n");
        }
    }

    // Cancela el pedido si aún no fue despachado ni entregado.
    @Override
    public void cancelar() {
        System.out.println("Cancelando " + this.tipoPedido + " " + this.idPedido + "...");

        if (this.estadoPedido.equalsIgnoreCase("Despachado") || this.estadoPedido.equalsIgnoreCase("Entregado")) {
            System.out.println("No se puede cancelar el pedido " + this.idPedido +
                    " porque ya fue despachado o entregado.\n");
            return;
        }

        this.estadoPedido = "Cancelado";
        System.out.println("Pedido " + this.idPedido + " cancelado correctamente.\n");
    }

    // Muestra el historial de pedidos entregados.
    @Override
    public void verHistorial() {
        this.gestorDatos.mostrarHistorial();
    }
}
