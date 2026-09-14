package model;

import java.util.concurrent.atomic.AtomicInteger;
public class Pedido {    
    private static final AtomicInteger contadorId = new AtomicInteger(0);
    private final int id;
    private final String direccionEntrega;
    private EstadoPedido estado;
    private final NivelUrgencia nivelUrgencia;

    public Pedido(String direccionEntrega, EstadoPedido estado, NivelUrgencia nivelUrgencia) {
        this.direccionEntrega = direccionEntrega;
        this.estado = estado;
        this.nivelUrgencia = nivelUrgencia;
        this.id = contadorId.incrementAndGet();
    }

    public int getId() {
        return id;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public NivelUrgencia getNivelUrgencia() {
        return nivelUrgencia;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    @Override 
    public String toString() {
        return  "Pedido #" + id + "\n"+
                "Destino:  " + direccionEntrega + "\n"+
                "Estado actual: " + estado + "\n"+
                "Nivel de urgencia: " + nivelUrgencia + "\n";
    }
    
}
