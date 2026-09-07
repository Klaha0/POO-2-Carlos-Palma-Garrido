package app;

import data.GestorDatos;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import model.Pedido;
import model.PedidoComida;
import model.PedidoEncomienda;
import model.PedidoExpress;
import model.Repartidor;

/**
 * Punto de entrada y demostración de las funcionalidades de SpeedFast.
 * Además de las validaciones del ciclo de vida de un pedido, simula una jornada
 * en la que varios repartidores entregan sus rutas en paralelo mediante hilos.
 */
public class Main {
    private static final GestorDatos gestorDatos = new GestorDatos();
    private static final ArrayList<Pedido> pedidos = gestorDatos.leerArchivo();

    /** Cantidad de repartidores que trabajan al mismo tiempo en la simulación. */
    private static final int REPARTIDORES_EN_TURNO = 3;

    /** Cantidad de pedidos que necesita la demostración completa. */
    private static final int PEDIDOS_REQUERIDOS = 11;

    /**
     * Pedido que se intenta cancelar una vez entregado. Se asigna dentro de
     * main() y no en la declaración, porque al cargarse la clase la colección
     * todavía puede estar vacía y buscarlo por índice haría fallar el arranque.
     */
    private static Pedido cancelaEntregado;

    public static void main(String[] args) {
        System.out.println("\n==== SPEEDFAST ====\n");
        System.out.println("Pedidos recuperados desde el archivo: " + pedidos.size() + "\n");

        // El archivo puede venir vacío, incompleto o con registros que se
        // descartaron por estar dañados, así que la colección se completa antes
        // de repartir los pedidos por índice.
        if (pedidos.size() < PEDIDOS_REQUERIDOS) {
            if (!pedidos.isEmpty()) {
                System.out.println("El archivo tiene menos pedidos de los que necesita la demostración: se crean los que faltan.\n");
            }
            crearPedidosDeDemostracion();
        }

        // Resguardo final: sin la cantidad mínima de pedidos la demostración no
        // puede continuar, y se avisa en lugar de fallar al buscarlos por índice.
        if (pedidos.size() < PEDIDOS_REQUERIDOS) {
            System.out.println("No fue posible preparar los " + PEDIDOS_REQUERIDOS
                    + " pedidos de la demostración. Se cancela la ejecución.");
            return;
        }

        // Permite repetir la demostración aunque los pedidos hayan quedado entregados.
        gestorDatos.reiniciarEstados();
        // Los seis primeros pedidos forman las rutas que se entregarán en paralelo.
        Pedido rutaLuis1 = pedidos.get(0);
        Pedido rutaLuis2 = pedidos.get(1);
        Pedido rutaDaniela1 = pedidos.get(2);
        Pedido rutaDaniela2 = pedidos.get(3);
        Pedido rutaMarco1 = pedidos.get(4);
        Pedido rutaMarco2 = pedidos.get(5);
        
        // Los pedidos siguientes sirven para probar las reglas de cancelación en
        // cada uno de los estados posibles.
        Pedido cancelaCreado = pedidos.get(6);
        Pedido cancelaReservado = pedidos.get(7);
        cancelaEntregado = pedidos.get(8);
        
        
        System.out.println("==== PRUEBAS DE CANCELACIÓN ====\n");
        
        cancelaCreado.cancelar();
        cancelaReservado.reservar();
        cancelaReservado.cancelar(); 

        System.out.println("==== PREPARACIÓN DE LA JORNADA ====\n");

        Repartidor luis = new Repartidor("Luis Díaz");
        Repartidor daniela = new Repartidor("Daniela Tapia");
        Repartidor marco = new Repartidor("Marco Fuentes");

        // Cada despacho encola el pedido en la cola de prioridad del repartidor.
        // Se entregan a propósito de más lejano a más cercano para comprobar que
        // la cola reordena la ruta y el hilo parte por el destino más próximo.
        prepararRuta(luis, rutaLuis2, rutaLuis1, cancelaEntregado);
        prepararRuta(daniela, rutaDaniela2, rutaDaniela1);
        prepararRuta(marco, rutaMarco2, rutaMarco1);

        System.out.println("==== SIMULACIÓN CONCURRENTE DE ENTREGAS ====\n");
        ejecutarEntregas(luis, daniela, marco);       

        // Reescribe el archivo completo con el estado final de la colección.
        gestorDatos.guardarTodos();
    }

    /**
     * Crea los pedidos que usa la demostración. Cada pedido se registra solo en
     * la colección de GestorDatos al construirse, y solo si no existen datos previos en el archivo.
     */
    private static void crearPedidosDeDemostracion() {
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
        new PedidoExpress("Argentina 7595", 9, 2);
    }

    /**
     * Reserva y despacha los pedidos de un repartidor para dejar su ruta lista
     * antes de que el hilo comience a trabajar.
     * @param repartidor repartidor que recibirá la ruta
     * @param asignados pedidos que componen la ruta
     */
    private static void prepararRuta(Repartidor repartidor, Pedido... asignados) {
        System.out.println("-- Ruta de " + repartidor.getNombre() + " --\n");
        for (Pedido pedido : asignados) {
            pedido.reservar();
            pedido.despachar(repartidor);
        }
    }

    /**
     * Ejecuta a todos los repartidores como hilos en paralelo mediante un
     * ExecutorService y espera a que la jornada completa termine.
     * @param repartidores repartidores que saldrán a entregar al mismo tiempo
     */
    private static void ejecutarEntregas(Repartidor... repartidores) {
        ExecutorService jornada = Executors.newFixedThreadPool(REPARTIDORES_EN_TURNO);

        for (Repartidor repartidor : repartidores) {
            jornada.execute(repartidor);
        }

        jornada.close();  
        cancelaEntregado.cancelar(); // Se prueba la cancelación de un pedido ya entregado.      
        try {
            if (!jornada.awaitTermination(30, TimeUnit.SECONDS)) {
                System.out.println("La jornada superó el tiempo máximo: se interrumpen las entregas pendientes.");
                jornada.shutdownNow();
            }
        } catch (InterruptedException e) {
            jornada.shutdownNow();
            Thread.currentThread().interrupt();
            System.out.println("La espera de la jornada fue interrumpida.");
        }

        System.out.println("\nTodos los repartidores terminaron sus entregas.\n");
        gestorDatos.mostrarCancelados();
        System.out.println();
        gestorDatos.mostrarHistorial();
    }
}
