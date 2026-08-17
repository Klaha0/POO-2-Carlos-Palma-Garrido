package model;

/**
 * Representa un pedido de comida en el sistema SpeedFast.
 * Hereda de Pedido y agrega funcionalidades específicas para entregas de comida,
 * incluyendo verificación de mochila térmica.
 */
public class PedidoComida extends Pedido {
    /** Descripción del pedido de comida */
    private String comida;

    /**
     * Constructor de PedidoComida.
     * @param direccionEntrega la dirección de entrega
     * @param comida descripción del pedido de comida
     */
    public PedidoComida(String direccionEntrega, String comida) {
        super(direccionEntrega);
        this.comida = comida;
    }

    /**
     * sobreescribe el método AsignarRepartidor
     * tambien utiliza el método de la super clase
     * @return un mensaje con el estado de asignación
     */
    @Override
    public String AsignarRepartidor() {
        return "[Pedido Comida]\n" +
                super.AsignarRepartidor() +
                "→ Verificando mochila térmica... OK\n";
    }

    /**
     * Asigna un repartidor específico al pedido de comida.
     * @param nombreRepartidor el nombre del repartidor asignado
     * @return un mensaje con los detalles de la asignación
     */
    public String AsignarRepartidor(String nombreRepartidor) {
        return  AsignarRepartidor() +
                "→ Su pedido ID: "+ super.getIdPedido() + "\n"+
                "→ Producto: " + this.comida + "\nha sido asignado a → "
                + nombreRepartidor + "\n" +
                "¡¡Que disfrutes tu comida!!\n";
    }
}
