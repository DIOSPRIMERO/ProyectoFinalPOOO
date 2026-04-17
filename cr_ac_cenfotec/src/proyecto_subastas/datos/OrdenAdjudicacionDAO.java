package proyecto_subastas.datos;

import proyecto_subastas.dominio.OrdenAdjudicacion;

import java.sql.*;

/**
 * Clase DAO que realiza operaciones sobre la tabla OrdenAdjudicacion en SQL Server.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class OrdenAdjudicacionDAO {

    /**
     * Inserta una orden de adjudicacion en la base de datos.
     *
     * @param idSubasta ID de la subasta que genero la orden.
     * @param orden     Orden a insertar.
     */
    public void insertar(int idSubasta, OrdenAdjudicacion orden) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "INSERT INTO OrdenAdjudicacion (idSubasta, nombreGanador, "
                    + "fechaOrden, precioTotal) VALUES (?,?,GETDATE(),?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idSubasta);
            ps.setString(2, orden.getNombreGanador());
            ps.setDouble(3, orden.getPrecioTotal());
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error insertar orden: " + e.getMessage());
        }
    }
}
