package modelo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.Objects;
public class Pedido {    
    private static final AtomicInteger contadorId = new AtomicInteger(0);
    private final int id;
    private final String direccionEntrega;
    private volatile EstadoPedido estado;
    private final TipoPedido tipo;
    private volatile String nombreRepartidor = "Sin asignar";

    public Pedido(String direccionEntrega, EstadoPedido estado, TipoPedido tipo) {
        if (direccionEntrega == null || direccionEntrega.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una dirección de entrega.");
        }
        this.direccionEntrega = direccionEntrega.trim();
        this.estado = Objects.requireNonNull(estado, "Debe indicar el estado.");
        this.tipo = Objects.requireNonNull(tipo, "Debe indicar el tipo de pedido.");
        this.id = contadorId.incrementAndGet();
    }

    public TipoPedido getTipo() {
        return tipo;
    }

    public String getNombreRepartidor() {
        return nombreRepartidor;
    }

    public void setNombreRepartidor(String nombreRepartidor) {
        this.nombreRepartidor = Objects.requireNonNull(nombreRepartidor);
    }

    public int getId() {
        return id;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public NivelUrgencia getNivelUrgencia() {
        return tipo.getNivelUrgencia();
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = Objects.requireNonNull(estado);
    }

    @Override 
    public String toString() {
        return  "Pedido #" + id + "\n"+
                "Destino:  " + direccionEntrega + "\n"+
                "Tipo: " + tipo + "\n"+
                "Estado actual: " + estado + "\n"+
                "Nivel de urgencia: " + getNivelUrgencia() + "\n";
    }
    
}
