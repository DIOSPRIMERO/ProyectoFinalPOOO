package proyecto_subastas.datos;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * Clase que gestiona la conexion a la base de datos SQL Server.
 * La informacion de conexion se lee desde el archivo db.properties
 * ubicado en el directorio raiz del proyecto (requisito de la consigna).
 *
 * Formato de la tira de conexion usada internamente:
 * jdbc:sqlserver://localhost:1433;databaseName=NOMBRE_BD;encrypt=false
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class ConexionDB {

    private static final String URL = "jdbc:sqlserver://KATLEEN\\SQLEXPRESS:1433;databaseName=SubastasDB;encrypt=false";
    private static final String USER = "sa";
    private static final String PASSWORD = "123";

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Error conexión: " + e.getMessage());
            return null;
        }
    }
}