package model;

/**
 * Interfaz que define las operaciones del ciclo de despacho y entrega.
 */
public interface Despachable {
    /**
     * Despacha el pedido y calcula su tiempo estimado.
     */
    void despachar();

    /**
     * Marca el pedido como entregado y registra la hora de entrega.
     */
    void entregar();
}
