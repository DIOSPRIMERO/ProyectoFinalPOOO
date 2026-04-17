package proyecto_subastas.control;

import proyecto_subastas.datos.CategoriaDAO;
import proyecto_subastas.datos.ObjetoSubastaDAO;
import proyecto_subastas.datos.OfertaDAO;
import proyecto_subastas.datos.OrdenAdjudicacionDAO;
import proyecto_subastas.datos.ServicioCorreo;
import proyecto_subastas.datos.SubastaDAO;
import proyecto_subastas.dominio.Categoria;
import proyecto_subastas.dominio.Coleccionista;
import proyecto_subastas.dominio.Moderador;
import proyecto_subastas.dominio.ObjetoSubasta;
import proyecto_subastas.dominio.Oferta;
import proyecto_subastas.dominio.OrdenAdjudicacion;
import proyecto_subastas.dominio.Subasta;
import proyecto_subastas.dominio.Usuario;
import proyecto_subastas.dominio.Vendedor;
import java.util.ArrayList;
import java.util.Random;

/**
 * Controlador que gestiona subastas, ofertas y categorias de la plataforma.
 * Aplica todas las reglas de negocio y delega la persistencia a los DAOs.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class ControladorSubasta {

    /** DAOs de persistencia. */
    private SubastaDAO subastaDAO;
    private OfertaDAO ofertaDAO;
    private ObjetoSubastaDAO objetoDAO;
    private CategoriaDAO categoriaDAO;
    private OrdenAdjudicacionDAO ordenDAO;

    /** Cache en memoria. */
    private ArrayList<Subasta> subastas;
    private ArrayList<Categoria> categorias;

    /**
     * Constructor. Carga datos existentes desde la BD.
     */
    public ControladorSubasta() {
        this.subastaDAO   = new SubastaDAO();
        this.ofertaDAO    = new OfertaDAO();
        this.objetoDAO    = new ObjetoSubastaDAO();
        this.categoriaDAO = new CategoriaDAO();
        this.ordenDAO     = new OrdenAdjudicacionDAO();

        this.subastas   = new ArrayList<>();
        this.categorias = new ArrayList<>();
        cargarDesdeDB();
    }

    /**
     * Carga subastas y categorias de la BD al iniciar.
     */
    private void cargarDesdeDB() {
        subastas   = subastaDAO.listar();
        categorias = categoriaDAO.listar();
    }

    /**
     * Crea un ObjetoSubasta (factory method para la UI).
     *
     * @param nombre      Nombre del objeto.
     * @param descripcion Descripcion.
     * @param estado      Estado de conservacion.
     * @param anioCompra  Anio de compra.
     * @return Nueva instancia de ObjetoSubasta.
     */
    public ObjetoSubasta crearObjeto(String nombre, String descripcion,
                                     String estado, String fechaCompra) {
        return new ObjetoSubasta(nombre, descripcion, estado, fechaCompra);
    }

    /**
     * Crea una nueva subasta aplicando todas las reglas de negocio.
     * Persiste en BD y retorna mensaje de resultado.
     *
     * @param creador          Usuario creador (Vendedor o Coleccionista).
     * @param objetos          Lista de objetos a subastar.
     * @param precioMinimo     Precio minimo de aceptacion.
     * @param fechaVencimiento Fecha de vencimiento (dd/mm/aaaa).
     * @return Mensaje de exito o error con prefijo "ERROR:".
     */
    public String crearSubasta(Usuario creador, ArrayList<ObjetoSubasta> objetos,
                               double precioMinimo, String fechaVencimiento) {
        if (creador instanceof Moderador) {
            return "ERROR: El moderador no puede crear subastas.";
        }
        if (objetos == null || objetos.isEmpty()) {
            return "ERROR: La subasta debe tener al menos un objeto.";
        }
        if (creador instanceof Coleccionista) {
            Coleccionista col = (Coleccionista) creador;
            for (int i = 0; i < objetos.size(); i++) {
                if (!col.getColeccion().contains(objetos.get(i))) {
                    return "ERROR: El coleccionista solo puede subastar objetos de su coleccion.";
                }
            }
        }

        Subasta nueva = new Subasta(creador, objetos, precioMinimo, fechaVencimiento);

        // Buscar el idUsuario interno en BD
        // (el identificacion es la cedula, el idUsuario es el PK autoincremental)
        // Usamos la cedula para hacer el INSERT en la BD (el DAO hace JOIN interno)
        ArrayList<Integer> idsObjetos = new ArrayList<>();
        for (ObjetoSubasta obj : objetos) {
            int idObj = objetoDAO.insertar(obj);
            if (idObj != -1) idsObjetos.add(idObj);
        }

        // Necesitamos el idUsuario (PK) del creador en la BD
        // Lo obtenemos haciendo una consulta por identificacion
        int idCreadorBD = obtenerIdUsuarioBD(creador.getIdentificacion());
        int idSubastaBD = subastaDAO.insertar(nueva, idCreadorBD, idsObjetos);

        if (idSubastaBD != -1) {
            subastas.add(nueva);
            return "Subasta #" + nueva.getId() + " creada correctamente.";
        }
        return "ERROR: No se pudo crear la subasta en la base de datos.";
    }

    /**
     * Registra una oferta en una subasta activa aplicando todas las reglas de negocio.
     *
     * @param idSubasta ID de la subasta (ID en memoria).
     * @param oferente  Usuario que oferta (debe ser Coleccionista).
     * @param monto     Monto de la oferta.
     * @return Mensaje de exito o error.
     */
    public String registrarOferta(int idSubasta, Usuario oferente, double monto) {
        Subasta subasta = buscarPorId(idSubasta);
        if (subasta == null) return "ERROR: No existe una subasta con ese ID.";
        if (!subasta.getEstado().equals("Activa")) return "ERROR: La subasta no esta activa.";
        if (oferente instanceof Moderador) return "ERROR: El moderador no puede hacer ofertas.";
        if (oferente instanceof Vendedor) return "ERROR: Los vendedores no pueden hacer ofertas.";
        if (subasta.getCreador().getIdentificacion().equals(oferente.getIdentificacion())) {
            return "ERROR: El creador no puede ofertar en su propia subasta.";
        }
        if (monto < subasta.getPrecioMinimo()) {
            return "ERROR: El monto es menor al precio minimo ($" + subasta.getPrecioMinimo() + ").";
        }

        Coleccionista col = (Coleccionista) oferente;
        Oferta oferta = new Oferta(col.getNombreCompleto(), col.getPuntuacion(), monto);
        subasta.getOfertas().add(oferta);

        // Persistir en BD - buscar idSubasta real en BD
        int idSubastaBD = subastaDAO.obtenerUltimoIdDeCreador(subasta.getCreador().getIdentificacion());
        if (idSubastaBD != -1) {
            ofertaDAO.insertar(idSubastaBD, oferta);
        }

        return "Oferta de $" + monto + " registrada en la subasta #" + idSubasta + ".";
    }

    /**
     * Marca una subasta activa como cerrada.
     *
     * @param idSubasta ID de la subasta.
     * @return Mensaje de exito o error.
     */
    public String cerrarSubasta(int idSubasta) {
        Subasta subasta = buscarPorId(idSubasta);
        if (subasta == null) return "ERROR: No existe una subasta con ese ID.";
        if (!subasta.getEstado().equals("Activa")) return "ERROR: Solo se pueden cerrar subastas activas.";
        subasta.setEstado("Cerrada");
        persistirEstadoSubasta(subasta, "Cerrada");
        return "Subasta #" + idSubasta + " marcada como cerrada.";
    }

    /**
     * Adjudica automaticamente el ganador de una subasta cerrada.
     *
     * @param idSubasta ID de la subasta.
     * @return Mensaje con el resultado.
     */
    public String adjudicarGanador(int idSubasta) {
        Subasta subasta = buscarPorId(idSubasta);
        if (subasta == null) return "ERROR: No existe una subasta con ese ID.";
        if (!subasta.getEstado().equals("Cerrada")) return "ERROR: Solo se pueden adjudicar subastas cerradas.";
        if (subasta.getOfertas().isEmpty()) return "La subasta cerro sin ofertas. No hay ganador.";

        Oferta ganadora = subasta.getOfertas().get(0);
        for (int i = 1; i < subasta.getOfertas().size(); i++) {
            if (subasta.getOfertas().get(i).getPrecioOfertado() > ganadora.getPrecioOfertado()) {
                ganadora = subasta.getOfertas().get(i);
            }
        }
        subasta.setOfertaGanadora(ganadora);
        subasta.setEstado("Adjudicada");
        persistirEstadoSubasta(subasta, "Adjudicada");
        return "Subasta #" + idSubasta + " adjudicada a: "
                + ganadora.getNombreOferente() + " con oferta de $" + ganadora.getPrecioOfertado();
    }

    /**
     * Registra la aceptacion de adjudicacion y genera la orden de compra.
     *
     * @param idSubasta     ID de la subasta.
     * @param nombreGanador Nombre del ganador.
     * @return Texto de la orden generada o mensaje de error.
     */
    public String aceptarAdjudicacion(int idSubasta, String nombreGanador) {
        Subasta subasta = buscarPorId(idSubasta);
        if (subasta == null) return "ERROR: No existe una subasta con ese ID.";
        if (!subasta.getEstado().equals("Adjudicada")) return "ERROR: La subasta no ha sido adjudicada aun.";
        if (subasta.getOfertaGanadora() == null) return "ERROR: No existe una oferta ganadora.";
        if (!subasta.getOfertaGanadora().getNombreOferente().equals(nombreGanador)) {
            return "ERROR: Usted no es el ganador de esta subasta.";
        }
        OrdenAdjudicacion orden = new OrdenAdjudicacion(
            nombreGanador, subasta.getObjetos(), subasta.getOfertaGanadora().getPrecioOfertado()
        );
        subasta.setOrden(orden);
        subasta.setAdjudicacionAceptada(true);
        subasta.setEstado("Completada");
        persistirEstadoSubasta(subasta, "Completada");

        // Persistir orden en BD
        int idSubastaBD = subastaDAO.obtenerUltimoIdDeCreador(subasta.getCreador().getIdentificacion());
        if (idSubastaBD != -1) {
            ordenDAO.insertar(idSubastaBD, orden);
        }
        return orden.toString();
    }

    /**
     * Confirma la entrega de objetos al ganador.
     *
     * @param idSubasta ID de la subasta.
     * @return Mensaje de exito o error.
     */
    public String confirmarEntrega(int idSubasta) {
        Subasta subasta = buscarPorId(idSubasta);
        if (subasta == null) return "ERROR: No existe una subasta con ese ID.";
        if (!subasta.isAdjudicacionAceptada()) return "ERROR: La adjudicacion no ha sido aceptada aun.";
        subasta.setEntregaConfirmada(true);
        persistirFlagsSubasta(subasta);
        return "Entrega confirmada para la subasta #" + idSubasta + ". Ya puede calificar.";
    }

    /**
     * Registra una calificacion de una parte hacia la otra.
     *
     * @param idSubasta    ID de la subasta.
     * @param calificacion Valor entre 1 y 5.
     * @param esGanador    true si califica el ganador; false si califica el creador.
     * @return Mensaje de exito o error.
     */
    public String calificar(int idSubasta, int calificacion, boolean esGanador) {
        if (calificacion < 1 || calificacion > 5) return "ERROR: La calificacion debe estar entre 1 y 5.";
        Subasta subasta = buscarPorId(idSubasta);
        if (subasta == null) return "ERROR: No existe una subasta con ese ID.";
        if (!subasta.isEntregaConfirmada()) return "ERROR: Solo se puede calificar despues de confirmar la entrega.";
        if (esGanador) {
            subasta.setCalificacionGanador(calificacion);
            return "Calificacion del ganador registrada: " + calificacion + "/5";
        } else {
            subasta.setCalificacionVendedor(calificacion);
            return "Calificacion del creador registrada: " + calificacion + "/5";
        }
    }

    /**
     * Asigna un moderador aleatorio a la ultima subasta creada.
     *
     * @param subasta     Subasta a la que asignar moderador.
     * @param moderadores Lista de coleccionistas con rol moderador.
     * @return Mensaje de resultado.
     */
    public String asignarModeradorAleatorio(Subasta subasta, ArrayList<Coleccionista> moderadores) {
        if (moderadores == null || moderadores.isEmpty()) return "No hay moderadores disponibles.";
        Random random = new Random();
        Coleccionista mod = moderadores.get(random.nextInt(moderadores.size()));
        subasta.setModeradorAsignado(mod);
        // Persistir en BD
        int idSubastaBD = subastaDAO.obtenerUltimoIdDeCreador(subasta.getCreador().getIdentificacion());
        if (idSubastaBD != -1) {
            subastaDAO.asignarModerador(idSubastaBD, mod.getIdentificacion());
        }
        // Enviar correo electronico al moderador (requerido por la consigna)
        ServicioCorreo.notificarModeradorSubasta(
            mod.getCorreoElectronico(),
            mod.getNombreCompleto(),
            subasta.getId(),
            subasta.getFechaVencimiento()
        );
        return "Moderador asignado: " + mod.getNombreCompleto()
                + " | Notificacion enviada a: " + mod.getCorreoElectronico();
    }

    /**
     * Crea y registra una nueva categoria.
     *
     * @param nombre      Nombre de la categoria.
     * @param descripcion Descripcion.
     * @return Mensaje de exito o error.
     */
    public String crearCategoria(String nombre, String descripcion) {
        if (nombre == null || nombre.trim().isEmpty()) return "ERROR: El nombre no puede estar vacio.";
        if (categoriaDAO.existePorNombre(nombre.trim())) return "ERROR: Ya existe una categoria con ese nombre.";
        Categoria nueva = new Categoria(nombre.trim(), descripcion);
        categoriaDAO.insertar(nueva);
        categorias.add(nueva);
        return "Categoria '" + nombre + "' creada correctamente.";
    }

    /**
     * Lista todas las categorias en texto.
     *
     * @return Cadena con las categorias o mensaje de lista vacia.
     */
    public String listarCategorias() {
        if (categorias.isEmpty()) return "No hay categorias registradas.";
        String lista = "";
        for (int i = 0; i < categorias.size(); i++) {
            lista += categorias.get(i).toString() + "\n";
        }
        return lista;
    }

    /**
     * Lista todas las subastas en texto.
     *
     * @return Cadena con las subastas o mensaje de lista vacia.
     */
    public String listarSubastas() {
        if (subastas.isEmpty()) return "No hay subastas registradas.";
        String lista = "";
        for (int i = 0; i < subastas.size(); i++) {
            lista += subastas.get(i).toString() + "\n";
        }
        return lista;
    }

    /**
     * Lista todas las ofertas agrupadas por subasta.
     *
     * @return Cadena con las ofertas o mensaje de lista vacia.
     */
    public String listarOfertas() {
        String lista = "";
        boolean hay = false;
        for (int i = 0; i < subastas.size(); i++) {
            Subasta s = subastas.get(i);
            if (!s.getOfertas().isEmpty()) {
                hay = true;
                lista += "-- Subasta #" + s.getId() + " --\n";
                for (int j = 0; j < s.getOfertas().size(); j++) {
                    lista += "  " + (j + 1) + ". " + s.getOfertas().get(j).toString() + "\n";
                }
            }
        }
        return hay ? lista : "No hay ofertas registradas.";
    }

    /**
     * Lista las subastas donde el usuario es creador.
     *
     * @param nombreUsuario Nombre del usuario.
     * @return Cadena con las subastas o mensaje de lista vacia.
     */
    public String listarMisSubastas(String nombreUsuario) {
        String lista = "";
        boolean hay = false;
        for (int i = 0; i < subastas.size(); i++) {
            if (subastas.get(i).getCreador().getNombreCompleto().equals(nombreUsuario)) {
                hay = true;
                lista += subastas.get(i).toString() + "\n";
            }
        }
        return hay ? lista : "No tiene subastas registradas.";
    }

    /**
     * Lista las subastas donde el usuario ha participado como oferente.
     *
     * @param nombreOferente Nombre del oferente.
     * @return Cadena con las subastas o mensaje de lista vacia.
     */
    public String listarSubastasComoOferente(String nombreOferente) {
        String lista = "";
        boolean hay = false;
        for (int i = 0; i < subastas.size(); i++) {
            Subasta s = subastas.get(i);
            for (int j = 0; j < s.getOfertas().size(); j++) {
                if (s.getOfertas().get(j).getNombreOferente().equals(nombreOferente)) {
                    hay = true;
                    lista += s.toString() + "\n";
                    break;
                }
            }
        }
        return hay ? lista : "No ha participado en ninguna subasta.";
    }

    /**
     * Busca una subasta por su ID en memoria.
     *
     * @param id ID de la subasta.
     * @return La subasta o null.
     */
    public Subasta buscarPorId(int id) {
        for (int i = 0; i < subastas.size(); i++) {
            if (subastas.get(i).getId() == id) return subastas.get(i);
        }
        return null;
    }

    /**
     * Retorna la lista de subastas en memoria.
     *
     * @return Lista de subastas.
     */
    public ArrayList<Subasta> getSubastas() {
        return subastas;
    }

    /**
     * Retorna la lista de categorias en memoria.
     *
     * @return Lista de categorias.
     */
    public ArrayList<Categoria> getCategorias() {
        return categorias;
    }

    // ── Metodos privados de persistencia ──────────────────────────

    /** Actualiza el estado de una subasta en la BD. */
    private void persistirEstadoSubasta(Subasta s, String estado) {
        int idBD = subastaDAO.obtenerUltimoIdDeCreador(s.getCreador().getIdentificacion());
        if (idBD != -1) subastaDAO.actualizarEstado(idBD, estado);
    }

    /** Actualiza los flags de adjudicacion/entrega en la BD. */
    private void persistirFlagsSubasta(Subasta s) {
        int idBD = subastaDAO.obtenerUltimoIdDeCreador(s.getCreador().getIdentificacion());
        if (idBD != -1) subastaDAO.actualizarFlags(idBD, s.isAdjudicacionAceptada(), s.isEntregaConfirmada());
    }

    /** Obtiene el ID interno (PK) de un usuario en la BD por su identificacion. */
    private int obtenerIdUsuarioBD(String identificacion) {
        // Usamos una consulta directa al DAO para obtener el PK interno
        try (java.sql.Connection conn = proyecto_subastas.datos.ConexionDB.conectar()) {
            if (conn == null) return -1;
            String sql = "SELECT idUsuario FROM Usuario WHERE identificacion = ?";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, identificacion);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("idUsuario");
        } catch (Exception e) {
            System.out.println("Error obtener id usuario BD: " + e.getMessage());
        }
        return -1;
    }
}
