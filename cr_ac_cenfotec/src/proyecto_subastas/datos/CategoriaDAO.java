package proyecto_subastas.datos;

import proyecto_subastas.dominio.Categoria;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase DAO que realiza operaciones CRUD sobre la tabla Categoria en SQL Server.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class CategoriaDAO {

    /**
     * Inserta una nueva categoria en la base de datos.
     *
     * @param c Categoria a insertar.
     */
    public void insertar(Categoria c) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "INSERT INTO Categoria (nombre, descripcion) VALUES (?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDescripcion());
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error insertar categoria: " + e.getMessage());
        }
    }

    /**
     * Retorna todas las categorias registradas en la BD.
     *
     * @return Lista de categorias.
     */
    public ArrayList<Categoria> listar() {
        ArrayList<Categoria> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar()) {

            String sql = "SELECT * FROM Categoria";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                Categoria c = new Categoria(
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                );
                lista.add(c);
            }

        } catch (Exception e) {
            System.out.println("Error listar categorias: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Verifica si ya existe una categoria con ese nombre (insensible a mayusculas).
     *
     * @param nombre Nombre a verificar.
     * @return true si ya existe.
     */
    public boolean existePorNombre(String nombre) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "SELECT COUNT(*) FROM Categoria WHERE LOWER(nombre) = LOWER(?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            System.out.println("Error verificar categoria: " + e.getMessage());
        }
        return false;
    }
}
