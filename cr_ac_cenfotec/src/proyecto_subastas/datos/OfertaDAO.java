package proyecto_subastas.datos;

import proyecto_subastas.dominio.Oferta;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase DAO que realiza operaciones sobre la tabla Oferta en SQL Server.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class OfertaDAO {

    /**
     * Inserta una nueva oferta en la base de datos.
     *
     * @param idSubasta ID de la subasta a la que pertenece la oferta.
     * @param o         Oferta a insertar.
     */
    public void insertar(int idSubasta, Oferta o) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "INSERT INTO Oferta (idSubasta, nombreOferente, puntuacionOferente, "
                    + "precioOfertado, fechaOferta) VALUES (?,?,?,?,GETDATE())";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idSubasta);
            ps.setString(2, o.getNombreOferente());
            ps.setDouble(3, o.getPuntuacionOferente());
            ps.setDouble(4, o.getPrecioOfertado());
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error insertar oferta: " + e.getMessage());
        }
    }

    /**
     * Retorna todas las ofertas de una subasta especifica.
     *
     * @param idSubasta ID de la subasta.
     * @return Lista de ofertas ordenadas por precio descendente.
     */
    public ArrayList<Oferta> listarPorSubasta(int idSubasta) {
        ArrayList<Oferta> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar()) {

            String sql = "SELECT * FROM Oferta WHERE idSubasta = ? ORDER BY precioOfertado DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idSubasta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Oferta o = new Oferta(
                    rs.getString("nombreOferente"),
                    rs.getDouble("puntuacionOferente"),
                    rs.getDouble("precioOfertado")
                );
                lista.add(o);
            }

        } catch (Exception e) {
            System.out.println("Error listar ofertas: " + e.getMessage());
        }

        return lista;
    }
}
