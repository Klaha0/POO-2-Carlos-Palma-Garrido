package modelo;

public enum NivelUrgencia {
    BAJA(1),
    MEDIA(2),
    ALTA(3);

    private final int prioridad;

    NivelUrgencia(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getPrioridad() {
        return prioridad;
    }
}
