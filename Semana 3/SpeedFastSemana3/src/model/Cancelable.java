package model;

/**
 * Interfaz que define el comportamiento de cancelación de un pedido.
 */
public interface Cancelable {
    /** 
     * Cancela un pedido que aún no ha sido despachado. 
     */
    void cancelar();
}
