package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/speedfast_db";
    private static final String USUARIO = "AQUI-USUARIO";
    private static final String CLAVE = "AQUI-CLAVE";

    /**
     * Traduce el estado SQL a un mensaje comprensible conservando la causa original.
     *
     * @param error excepción producida durante la persistencia.
     * @return la excepción que mostrarán los formularios.
     */
    public static IllegalStateException errorPersistencia(SQLException error) {
        String estado = error.getSQLState();
        String mensaje = estado != null && estado.startsWith("08")
                ? "No se pudo conectar con MySQL. Revisa el servidor y la configuración."
                : estado != null && estado.startsWith("23")
                ? "La operación incumple una relación o un registro duplicado. Revisa las entregas asociadas."
                : estado != null && estado.startsWith("22")
                ? "Los datos no coinciden con el formato o longitud admitidos por la base de datos."
                : "No se pudo completar la operación en MySQL. Detalle: " + error.getMessage();
        return new IllegalStateException(mensaje, error);
    }

    /**
     * Prepara una sentencia SQL parametrizada usando la conexión de la operación.
     *
     * @param conexion conexión abierta de la operación.
     * @param consulta sentencia SQL con parámetros JDBC.
     * @return el statement preparado; quien lo utiliza debe cerrarlo.
     * @throws SQLException si falla la preparación de la sentencia.
     */
    public static PreparedStatement preparar(Connection conexion, String consulta) throws SQLException {
        return conexion.prepareStatement(consulta);
    }

    /**
     * Abre una conexión con la base de datos configurada.
     *
     * @return la conexión abierta; quien la utiliza debe cerrarla.
     * @throws SQLException si MySQL rechaza la conexión o el servidor no está disponible.
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CLAVE);
    }
}
