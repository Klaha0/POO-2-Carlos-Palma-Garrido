package app;

import data.GestorDatos;
import java.util.ArrayList;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;

/**
 * Punto de entrada y demostración de las funcionalidades de SpeedFast.
 */
public class Main {
    private static final GestorDatos gestorDatos = new GestorDatos();
    private static final ArrayList<Pedido> pedidos = gestorDatos.leerArchivo();

    public static void main(String[] args) {
        System.out.println("\n==== SPEEDFAST ====\n");
        System.out.println("Pedidos recuperados desde el archivo: " + pedidos.size() + "\n");

        // La primera ejecución crea los pedidos que se usan en la demostración.
        if (pedidos.isEmpty()) {
            new PedidoComida("Los Arrayanes 568", 3, "Pizza Hawaiana");
            new PedidoEncomienda("Santa Rosa 567", 7, "Paquete de libros");
            new PedidoExpress("Coloso 1584", 6, 5);
            new PedidoComida("Grecia 1450", 8, "Lasaña vegetariana");
            new PedidoExpress("Flanders 7683", 5, 3);
            new PedidoEncomienda("Salar de Atacama 7958", 10, "Caja de herramientas");
            new PedidoComida("Los Carrera 120", 4, "Sushi 30 piezas");
            new PedidoExpress("Brasil 230", 2, 8);
            new PedidoEncomienda("Pasaje Los Robles 45", 12, "Documentos importantes");
            new PedidoComida("Calle Nueva 900", 15, "Pollo asado");
            new PedidoExpress("Argentina 1234", 9, 2);
        }

        // Permite repetir la demostración aunque los pedidos hayan quedado entregados.
        gestorDatos.reiniciarEstados();

        Pedido entregaAutomatica = pedidos.get(0);
        Pedido entregaManual = pedidos.get(1);
        Pedido cancelacionReservada = pedidos.get(2);
        Pedido sinRepartidor = pedidos.get(3);
        Pedido entregaSinDespachar = pedidos.get(4);
        Pedido reservaRepetida = pedidos.get(5);
        Pedido repartidorEnBlanco = pedidos.get(6);
        Pedido despachoDirecto = pedidos.get(7);
        Pedido cancelacionSinReservar = pedidos.get(8);
        Pedido despachoDeCancelado = pedidos.get(9);
        Pedido entregaRepetida = pedidos.get(10);

        System.out.println("==== RESUMEN DE PEDIDOS ====\n");
        for (Pedido pedido : pedidos) {
            pedido.mostrarResumen();
        }

        System.out.println("==== RESERVA Y ASIGNACIÓN AUTOMÁTICA ====\n");
        entregaAutomatica.reservar();
        entregaAutomatica.asignarRepartidor();
        entregaAutomatica.despachar();
        entregaAutomatica.entregar();

        System.out.println("==== ASIGNACIÓN MANUAL Y ENTREGA ====\n");
        entregaManual.reservar();
        entregaManual.asignarRepartidor("Daniela Tapia");
        entregaManual.despachar();
        entregaManual.entregar();

        System.out.println("==== DESPACHO SIN RESERVA PREVIA ====\n");
        despachoDirecto.asignarRepartidor();
        despachoDirecto.despachar();
        despachoDirecto.entregar();

        System.out.println("==== CANCELACIÓN DE PEDIDOS ====\n");
        cancelacionReservada.reservar();
        cancelacionReservada.cancelar();
        cancelacionSinReservar.cancelar();

        System.out.println("==== CASOS RECHAZADOS ====\n");

        // No se puede despachar un pedido que todavía no tiene repartidor.
        sinRepartidor.reservar();
        sinRepartidor.despachar();

        // No se puede entregar un pedido que aún no fue despachado.
        entregaSinDespachar.asignarRepartidor();
        entregaSinDespachar.entregar();

        // Un pedido reservado no se puede volver a reservar.
        reservaRepetida.reservar();
        reservaRepetida.reservar();

        // La asignación manual exige un nombre válido.
        repartidorEnBlanco.asignarRepartidor("   ");

        // Un pedido cancelado ya no se puede despachar.
        despachoDeCancelado.asignarRepartidor();
        despachoDeCancelado.cancelar();
        despachoDeCancelado.despachar();

        // Un pedido entregado no se puede entregar ni cancelar de nuevo.
        entregaRepetida.asignarRepartidor();
        entregaRepetida.despachar();
        entregaRepetida.entregar();
        entregaRepetida.entregar();
        entregaAutomatica.cancelar();

        entregaAutomatica.verHistorial();

        // Reescribe el archivo completo con el estado final de la colección.
        gestorDatos.guardarTodos();
    }
}
