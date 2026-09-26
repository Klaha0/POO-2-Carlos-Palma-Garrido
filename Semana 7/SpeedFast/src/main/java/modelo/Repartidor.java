package modelo;

import java.util.Objects;

/** Datos de un repartidor almacenado en la base de datos. */
public class Repartidor {
    private final int id;
    private final String nombre;

    public Repartidor(int id, String nombre) {
        this.id = Objects.requireNonNull(id);
        this.nombre = Objects.requireNonNull(nombre);
    }

    public String getNombre() { return nombre; }
    public int getId() { return id; }
}
