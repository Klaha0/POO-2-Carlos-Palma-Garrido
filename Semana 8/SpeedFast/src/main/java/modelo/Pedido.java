package modelo;

import java.util.Objects;
public class Pedido {    
    private final int id;
    private final String direccion;
    private EstadoPedido estado;
    private final TipoPedido tipo;

    public Pedido(int id, String direccion, EstadoPedido estado, TipoPedido tipo) {
        if (direccion == null || direccion.isBlank()) {
            throw new IllegalArgumentException("Debe ingresar una dirección de entrega.");
        }
        this.direccion = direccion.trim();
        this.estado = Objects.requireNonNull(estado, "Debe indicar el estado.");
        this.tipo = Objects.requireNonNull(tipo, "Debe indicar el tipo de pedido.");
        this.id = id;
    }

    public TipoPedido getTipo() {
        return tipo;
    }

    public int getId() {
        return id;
    }

    public String getDireccion() {
        return this.direccion;
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
                "Destino:  " + direccion + "\n"+
                "Tipo: " + tipo + "\n"+
                "Estado actual: " + estado + "\n"+
                "Nivel de urgencia: " + getNivelUrgencia() + "\n";
    }
    
}
