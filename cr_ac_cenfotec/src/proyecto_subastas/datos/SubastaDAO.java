package proyecto_subastas.datos;

import proyecto_subastas.dominio.ObjetoSubasta;
import proyecto_subastas.dominio.Oferta;
import proyecto_subastas.dominio.Subasta;
import proyecto_subastas.dominio.Usuario;

import java.sql.*;
import java.util.ArrayList;

/**
 * Clase DAO que realiza operaciones CRUD sobre la tabla Subasta en SQL Server.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class SubastaDAO {

    /**
     * Inserta una subasta en la BD junto con sus objetos asociados.
     * Retorna el ID autogenerado por SQL Server.
     *
     * @param s              Subasta a insertar.
     * @param idCreador      ID interno del creador en la tabla Usuario.
     * @param idsObjetos     Lista de IDs de los objetos ya insertados en ObjetoSubasta.
     * @return ID de la subasta creada, o -1 si fallo.
     */
    public int insertar(Subasta s, int idCreador, ArrayList<Integer> idsObjetos) {
        try (Connection conn = ConexionDB.conectar()) {

            // Insertar la subasta
            String sql = "INSERT INTO Subasta (idCreador, fechaVencimiento, precioMinimo, estado) "
                    + "VALUES (?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idCreador);
            ps.setString(2, s.getFechaVencimiento());
            ps.setDouble(3, s.getPrecioMinimo());
            ps.setString(4, s.getEstado());
            ps.executeUpdate();

            // Obtener ID generado
            int idSubasta = -1;
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idSubasta = rs.getInt(1);
            }

            // Insertar relacion SubastaObjeto
            if (idSubasta != -1) {
                for (int idObjeto : idsObjetos) {
                    String sqlRel = "INSERT INTO SubastaObjeto (idSubasta, idObjeto) VALUES (?,?)";
                    PreparedStatement psRel = conn.prepareStatement(sqlRel);
                    psRel.setInt(1, idSubasta);
                    psRel.setInt(2, idObjeto);
                    psRel.executeUpdate();
                }
            }

            return idSubasta;

        } catch (Exception e) {
            System.out.println("Error insertar subasta: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Retorna todas las subastas registradas en la BD (sin objetos ni ofertas cargados).
     * Los objetos y ofertas se cargan por separado si se necesitan.
     *
     * @return Lista de subastas con datos basicos.
     */
    public ArrayList<Subasta> listar() {
        ArrayList<Subasta> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.conectar()) {

            // JOIN con Usuario para tener el nombre del creador en el toString
            String sql = "SELECT s.idSubasta, s.fechaVencimiento, s.precioMinimo, "
                    + "s.estado, s.adjudicacionAceptada, s.entregaConfirmada, "
                    + "u.nombreCompleto as nombreCreador, u.identificacion as idCreador "
                    + "FROM Subasta s "
                    + "INNER JOIN Usuario u ON s.idCreador = u.idUsuario";

            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                // Creador temporal para mostrar en toString
                Usuario creadorTemp = new proyecto_subastas.dominio.Vendedor();
                creadorTemp.setNombreCompleto(rs.getString("nombreCreador"));
                creadorTemp.setIdentificacion(rs.getString("idCreador"));

                ArrayList<ObjetoSubasta> objsVacios = new ArrayList<>();
                Subasta sub = new Subasta(
                    creadorTemp, objsVacios,
                    rs.getDouble("precioMinimo"),
                    rs.getString("fechaVencimiento")
                );
                sub.setEstado(rs.getString("estado"));
                sub.setAdjudicacionAceptada(rs.getBoolean("adjudicacionAceptada"));
                sub.setEntregaConfirmada(rs.getBoolean("entregaConfirmada"));

                // Cargar ofertas de esta subasta
                ArrayList<Oferta> ofertas = new OfertaDAO().listarPorSubasta(rs.getInt("idSubasta"));
                sub.setOfertas(ofertas);

                // Cargar objetos de esta subasta
                ArrayList<ObjetoSubasta> objs = new ObjetoSubastaDAO().listarPorSubasta(rs.getInt("idSubasta"));
                sub.setObjetos(objs);

                lista.add(sub);
            }

        } catch (Exception e) {
            System.out.println("Error listar subastas: " + e.getMessage());
        }

        return lista;
    }

    /**
     * Actualiza el estado de una subasta (Activa, Cerrada, Adjudicada, Completada).
     *
     * @param idSubasta ID de la subasta.
     * @param estado    Nuevo estado.
     */
    public void actualizarEstado(int idSubasta, String estado) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "UPDATE Subasta SET estado=? WHERE idSubasta=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, estado);
            ps.setInt(2, idSubasta);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error actualizar estado subasta: " + e.getMessage());
        }
    }

    /**
     * Actualiza los flags de adjudicacion y entrega de una subasta.
     *
     * @param idSubasta              ID de la subasta.
     * @param adjudicacionAceptada   true si el ganador acepto.
     * @param entregaConfirmada      true si el ganador confirmo la entrega.
     */
    public void actualizarFlags(int idSubasta,
                                boolean adjudicacionAceptada,
                                boolean entregaConfirmada) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "UPDATE Subasta SET adjudicacionAceptada=?, entregaConfirmada=? "
                    + "WHERE idSubasta=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, adjudicacionAceptada);
            ps.setBoolean(2, entregaConfirmada);
            ps.setInt(3, idSubasta);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error actualizar flags subasta: " + e.getMessage());
        }
    }

    /**
     * Retorna el ID interno (idSubasta) de la ultima subasta insertada por un creador.
     * Se usa para obtener el ID real de la BD despues de insertar.
     *
     * @param identificacionCreador Cedula del creador.
     * @return idSubasta del ultimo registro del creador, o -1 si no hay.
     */
    public int obtenerUltimoIdDeCreador(String identificacionCreador) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "SELECT TOP 1 s.idSubasta FROM Subasta s "
                    + "INNER JOIN Usuario u ON s.idCreador = u.idUsuario "
                    + "WHERE u.identificacion = ? "
                    + "ORDER BY s.idSubasta DESC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, identificacionCreador);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("idSubasta");
            }

        } catch (Exception e) {
            System.out.println("Error obtener ultimo id subasta: " + e.getMessage());
        }
        return -1;
    }

    /**
     * Actualiza el moderador asignado a una subasta.
     *
     * @param idSubasta              ID de la subasta.
     * @param idModeradorIdentificacion Cedula del moderador asignado.
     */
    public void asignarModerador(int idSubasta, String idModeradorIdentificacion) {
        try (Connection conn = ConexionDB.conectar()) {

            String sql = "UPDATE Subasta SET idModeradorAsignado = "
                    + "(SELECT idUsuario FROM Usuario WHERE identificacion = ?) "
                    + "WHERE idSubasta = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, idModeradorIdentificacion);
            ps.setInt(2, idSubasta);
            ps.executeUpdate();

        } catch (Exception e) {
            System.out.println("Error asignar moderador: " + e.getMessage());
        }
    }
}
