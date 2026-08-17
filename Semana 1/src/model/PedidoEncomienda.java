package model;

/**
 * Representa un pedido de encomienda en el sistema SpeedFast.
 * Hereda de Pedido y agrega funcionalidades específicas para entregas de encomiendas,
 * incluyendo validación de embalaje y peso.
 */
public class PedidoEncomienda extends Pedido {
    /** Indica si la encomienda cumple con los estándares de embalaje y peso */
    private boolean validacionEmbalajeYPeso = false;
    /** Descripción del contenido de la encomienda */
    private String encomienda;

    /**
     * Constructor de PedidoEncomienda.
     * @param direccionEntrega la dirección de entrega
     * @param validacionEmbalajeYPeso indica si la encomienda cumple estándares de peso y embalaje
     * @param encomienda descripción del contenido de la encomienda
     */
    public PedidoEncomienda(String direccionEntrega, boolean validacionEmbalajeYPeso, String encomienda) {
        super(direccionEntrega);
        this.validacionEmbalajeYPeso  = validacionEmbalajeYPeso;
        this.encomienda =  encomienda;
    }

    /**
     * Verifica si la encomienda cumple con los estándares de embalaje y peso.
     * @return true si la validación es exitosa, false en caso contrario
     */
    public boolean validacionEmbalajeYPeso() {
        return validacionEmbalajeYPeso;
    }

    /**
     * Establece el estado de validación de embalaje y peso.
     * @param validacionEmbalajeYPeso true si cumple estándares, false en caso contrario
     */
    public void validacionEmbalajeYPeso(boolean validacionEmbalajeYPeso) {
        this.validacionEmbalajeYPeso = validacionEmbalajeYPeso;
    }

    /**
     * sobreescribe el método AsignarRepartidor
     * tambien utiliza el método de la super clase
     * @return un mensaje con el estado de asignación
     */
    @Override
    public String AsignarRepartidor() {
        return "[Pedido Encomienda]\n" +
                super.AsignarRepartidor() +
                "→ Validando peso y embalaje... OK\n";
    }

    /**
     * Asigna un repartidor específico al pedido de encomienda.
     * Solo asigna si la encomienda cumple con los estándares de validación.
     * @param nombreRepartidor el nombre del repartidor asignado
     * @return un mensaje con los detalles de la asignación o un mensaje de rechazo
     */
    public String AsignarRepartidor(String nombreRepartidor) {
          if(!validacionEmbalajeYPeso)
              return"Su encomienda no cumple con los estándares de peso o embalaje";

          return AsignarRepartidor() +
                "→ Su encomienda ID: "+ super.getIdPedido() + "\n"+
                "→ de: " + this.encomienda + "\nha sido asignado a → "
                + nombreRepartidor + "\n" +
                "¡¡Gracias por preferirnos!!\n";
    }
}
