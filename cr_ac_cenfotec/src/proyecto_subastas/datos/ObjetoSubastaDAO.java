package proyecto_subastas.datos;

import proyecto_subastas.dominio.ObjetoSubasta;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase DAO que realiza operaciones sobre la tabla ObjetoSubasta en SQL Server.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class ObjetoSubastaDAO {

    /**
     * Inserta un objeto en la BD y retorna el ID autogenerado.
     *
     * @param obj Objeto a insertar.
     * @return ID generado por la BD, o -1 si fallo.
     */
    public int insertar(ObjetoSubasta obj) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "INSERT INTO ObjetoSubasta (nombre, descripcion, estado, fechaCompra) "
                    + "VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, obj.getNombre());
            ps.setString(2, obj.getDescripcion());
            ps.setString(3, obj.getEstado());
            ps.setString(4, obj.getFechaCompra());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            System.out.println("Error insertar objeto: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retorna los objetos asociados a una subasta especifica.
     *
     * @param idSubasta ID de la subasta.
     * @return Lista de objetos de esa subasta.
     */
    public ArrayList<ObjetoSubasta> listarPorSubasta(int idSubasta) {
        ArrayList<ObjetoSubasta> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar()) {

            String sql = "SELECT o.* FROM ObjetoSubasta o "
                    + "INNER JOIN SubastaObjeto so ON o.idObjeto = so.idObjeto "
                    + "WHERE so.idSubasta = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, idSubasta);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ObjetoSubasta obj = new ObjetoSubasta(
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    rs.getString("estado"),
                    rs.getString("fechaCompra")
                );
                lista.add(obj);
            }

        } catch (Exception e) {
            System.out.println("Error listar objetos por subasta: " + e.getMessage());
        }

        return lista;
    }
}
