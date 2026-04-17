package proyecto_subastas.datos;

import proyecto_subastas.dominio.Coleccionista;
import proyecto_subastas.dominio.Moderador;
import proyecto_subastas.dominio.Usuario;
import proyecto_subastas.dominio.Vendedor;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase DAO que realiza operaciones CRUD sobre la tabla Usuario en SQL Server.
 * Sigue el patron DAO (Data Access Object) con PreparedStatement.
 *
 * Roles en BD: 1 = Moderador, 2 = Vendedor, 3 = Coleccionista
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class UsuarioDAO {

    /**
     * Inserta un nuevo usuario en la base de datos.
     *
     * @param u Usuario a insertar (Moderador, Vendedor o Coleccionista).
     */
    public void insertar(Usuario u) {
        try (Connection conn = ConexionDB.conectar()) {

            // Determinar idRol y campos especificos segun tipo
            int idRol;
            double puntuacion = 0;
            String direccion = "";
            boolean esModerador = false;

            if (u instanceof Moderador) {
                idRol = 1;
            } else if (u instanceof Vendedor) {
                idRol = 2;
                puntuacion = ((Vendedor) u).getPuntuacion();
                direccion = ((Vendedor) u).getDireccion();
            } else {
                idRol = 3;
                puntuacion = ((Coleccionista) u).getPuntuacion();
                direccion = ((Coleccionista) u).getDireccion();
                esModerador = ((Coleccionista) u).isEsModerador();
            }

            String sql = "INSERT INTO Usuario (nombreCompleto, identificacion, fechaNacimiento, "
                    + "contrasena, correoElectronico, activo, idRol, puntuacion, direccion, esModerador) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, u.getNombreCompleto());
            ps.setString(2, u.getIdentificacion());
            ps.setString(3, u.getFechaNacimiento());
            ps.setString(4, u.getContrasena());
            ps.setString(5, u.getCorreoElectronico());
            ps.setBoolean(6, u.isActivo());
            ps.setInt(7, idRol);
            ps.setDouble(8, puntuacion);
            ps.setString(9, direccion);
            ps.setBoolean(10, esModerador);

            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error insertar usuario: " + e.getMessage());
        }
    }

    /**
     * Retorna la lista de todos los usuarios registrados en la BD.
     *
     * @return Lista de objetos Usuario (Moderador, Vendedor o Coleccionista segun idRol).
     */
    public ArrayList<Usuario> listar() {
        ArrayList<Usuario> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar()) {

            String sql = "SELECT * FROM Usuario";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                int rol = rs.getInt("idRol");
                Usuario u;

                if (rol == 1) {
                    u = new Moderador(
                        rs.getString("nombreCompleto"),
                        rs.getString("identificacion"),
                        rs.getString("fechaNacimiento"),
                        rs.getString("contrasena"),
                        rs.getString("correoElectronico")
                    );
                } else if (rol == 2) {
                    u = new Vendedor(
                        rs.getString("nombreCompleto"),
                        rs.getString("identificacion"),
                        rs.getString("fechaNacimiento"),
                        rs.getString("contrasena"),
                        rs.getString("correoElectronico"),
                        rs.getDouble("puntuacion"),
                        rs.getString("direccion")
                    );
                } else {
                    Coleccionista c = new Coleccionista(
                        rs.getString("nombreCompleto"),
                        rs.getString("identificacion"),
                        rs.getString("fechaNacimiento"),
                        rs.getString("contrasena"),
                        rs.getString("correoElectronico"),
                        rs.getDouble("puntuacion"),
                        rs.getString("direccion")
                    );
                    c.setEsModerador(rs.getBoolean("esModerador"));
                    u = c;
                }

                u.setActivo(rs.getBoolean("activo"));
                lista.add(u);
            }

        } catch (Exception e) {
            System.out.println("Error listar usuarios: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Busca un usuario por su numero de identificacion.
     *
     * @param identificacion Cedula o ID del usuario.
     * @return El Usuario encontrado, o null si no existe.
     */
    public Usuario buscarPorIdentificacion(String identificacion) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "SELECT * FROM Usuario WHERE identificacion = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, identificacion);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int rol = rs.getInt("idRol");
                Usuario u;

                if (rol == 1) {
                    u = new Moderador(
                        rs.getString("nombreCompleto"), rs.getString("identificacion"),
                        rs.getString("fechaNacimiento"), rs.getString("contrasena"),
                        rs.getString("correoElectronico")
                    );
                } else if (rol == 2) {
                    u = new Vendedor(
                        rs.getString("nombreCompleto"), rs.getString("identificacion"),
                        rs.getString("fechaNacimiento"), rs.getString("contrasena"),
                        rs.getString("correoElectronico"),
                        rs.getDouble("puntuacion"), rs.getString("direccion")
                    );
                } else {
                    Coleccionista c = new Coleccionista(
                        rs.getString("nombreCompleto"), rs.getString("identificacion"),
                        rs.getString("fechaNacimiento"), rs.getString("contrasena"),
                        rs.getString("correoElectronico"),
                        rs.getDouble("puntuacion"), rs.getString("direccion")
                    );
                    c.setEsModerador(rs.getBoolean("esModerador"));
                    u = c;
                }
                u.setActivo(rs.getBoolean("activo"));
                return u;
            }

        } catch (Exception e) {
            System.out.println("Error buscar usuario: " + e.getMessage());
        }
        return null;
    }

    /**
     * Actualiza nombre, correo y direccion de un usuario existente.
     *
     * @param identificacion ID del usuario a actualizar.
     * @param nuevoNombre    Nuevo nombre completo.
     * @param nuevoCorreo    Nuevo correo electronico.
     * @param nuevaDireccion Nueva direccion.
     */
    public void actualizar(String identificacion, String nuevoNombre,
                           String nuevoCorreo, String nuevaDireccion) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "UPDATE Usuario SET nombreCompleto=?, correoElectronico=?, direccion=? "
                    + "WHERE identificacion=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevoCorreo);
            ps.setString(3, nuevaDireccion);
            ps.setString(4, identificacion);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error actualizar usuario: " + e.getMessage());
        }
    }

    /**
     * Cambia el estado activo/inactivo de un usuario.
     *
     * @param identificacion ID del usuario.
     * @param activo         true para activar, false para inactivar.
     */
    public void actualizarEstado(String identificacion, boolean activo) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "UPDATE Usuario SET activo=? WHERE identificacion=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, activo);
            ps.setString(2, identificacion);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error actualizar estado: " + e.getMessage());
        }
    }

    /**
     * Asigna o quita el rol de moderador de subastas a un coleccionista.
     *
     * @param identificacion ID del coleccionista.
     * @param esModerador    true para asignar el rol, false para quitarlo.
     */
    public void actualizarRolModerador(String identificacion, boolean esModerador) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "UPDATE Usuario SET esModerador=? WHERE identificacion=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, esModerador);
            ps.setString(2, identificacion);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error actualizar rol moderador: " + e.getMessage());
        }
    }

    /**
     * Verifica si ya existe un moderador del sistema en la BD (idRol = 1).
     *
     * @return true si existe al menos un moderador registrado.
     */
    public boolean existeModerador() {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "SELECT COUNT(*) FROM Usuario WHERE idRol = 1";
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            System.out.println("Error verificar moderador: " + e.getMessage());
        }
        return false;
    }
}
