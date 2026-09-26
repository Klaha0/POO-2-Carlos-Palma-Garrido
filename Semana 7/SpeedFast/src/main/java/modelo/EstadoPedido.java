package modelo;

public enum EstadoPedido {
    PENDIENTE,
    EN_REPARTO,
    ENTREGADO;

    @Override
    public String toString() {
        return switch (this) {
            case PENDIENTE -> "PENDIENTE";
            case EN_REPARTO -> "EN_REPARTO";
            case ENTREGADO -> "ENTREGADO";
            default -> throw new IllegalArgumentException("No se encontro el estado del pedido");
        };
    }
}