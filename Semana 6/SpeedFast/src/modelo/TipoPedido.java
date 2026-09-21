package modelo;

public enum TipoPedido {
    EXPRESS(NivelUrgencia.ALTA),
    COMIDA(NivelUrgencia.MEDIA),
    ENCOMIENDA(NivelUrgencia.BAJA);

    private final NivelUrgencia nivelUrgencia;

    TipoPedido(NivelUrgencia nivelUrgencia) {
        this.nivelUrgencia = nivelUrgencia;
    }

    public NivelUrgencia getNivelUrgencia() {
        return nivelUrgencia;
    }

    @Override
    public String toString() {
        return switch (this) {
            case EXPRESS -> "Express";
            case COMIDA -> "Comida";
            case ENCOMIENDA -> "Encomienda";
            default -> throw new IllegalArgumentException("No se encontro el tipo de pedido");
        };
    }
}
