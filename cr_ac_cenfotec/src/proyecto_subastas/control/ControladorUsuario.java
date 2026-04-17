package proyecto_subastas.control;

import proyecto_subastas.datos.UsuarioDAO;
import proyecto_subastas.dominio.Coleccionista;
import proyecto_subastas.dominio.Moderador;
import proyecto_subastas.dominio.Usuario;
import proyecto_subastas.dominio.Vendedor;
import java.util.ArrayList;

/**
 * Controlador que gestiona los usuarios de la plataforma de subastas.
 * Aplica todas las reglas de negocio y delega la persistencia al UsuarioDAO.
 * Esta clase representa la capa logica (Service) entre la UI y el DAO.
 *
 * Reglas de negocio aplicadas:
 * - RN2: Solo puede haber un unico moderador.
 * - RN7: Vendedores y coleccionistas deben ser mayores de edad.
 * - RN8: El moderador debe ser mayor de edad.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class ControladorUsuario {

    /** DAO para persistencia en SQL Server. */
    private UsuarioDAO dao;

    /** Cache en memoria de usuarios (para no consultar BD en cada operacion). */
    private ArrayList<Usuario> usuarios;

    /** Referencia al moderador del sistema. */
    private Moderador moderador;

    /**
     * Constructor. Carga los usuarios existentes desde la BD al iniciar.
     */
    public ControladorUsuario() {
        this.dao = new UsuarioDAO();
        this.usuarios = new ArrayList<>();
        cargarDesdeDB();
    }

    /**
     * Carga todos los usuarios de la BD a la lista en memoria.
     * Se llama una vez al iniciar la aplicacion.
     */
    private void cargarDesdeDB() {
        usuarios = dao.listar();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i) instanceof Moderador) {
                moderador = (Moderador) usuarios.get(i);
                break;
            }
        }
    }

    /**
     * Verifica si ya existe un moderador registrado en el sistema.
     *
     * @return true si hay moderador; false si no existe.
     */
    public boolean existeModerador() {
        return moderador != null;
    }

    /**
     * Registra un nuevo moderador en el sistema.
     * Valida que no exista otro moderador (RN2) y que sea mayor de edad (RN8).
     * La edad se calcula a partir de la fecha de nacimiento real.
     *
     * @param nombre     Nombre completo del moderador.
     * @param id         Numero de identificacion.
     * @param fechaNac   Fecha de nacimiento en formato "aaaa-MM-dd" (ej: "1990-05-15").
     * @param contrasena Contrasena de acceso.
     * @param correo     Correo electronico.
     * @return Mensaje de exito o error con prefijo "ERROR:".
     */
    public String registrarModerador(String nombre, String id, String fechaNac,
                                     String contrasena, String correo) {
        if (existeModerador()) {
            return "ERROR: Ya existe un moderador en el sistema.";
        }
        // Validar mayor de edad usando el metodo de la clase dominio (RN8)
        Moderador temp = new Moderador(nombre, id, fechaNac, contrasena, correo);
        if (!temp.esMayorDeEdad()) {
            return "ERROR: El moderador debe ser mayor de edad.";
        }
        moderador = temp;
        dao.insertar(moderador);
        usuarios.add(moderador);
        return "Moderador registrado correctamente.";
    }

    /**
     * Registra un nuevo vendedor en el sistema.
     * Valida que sea mayor de edad (RN7) y que no exista otro usuario con el mismo ID.
     *
     * @param nombre     Nombre completo del vendedor.
     * @param id         Numero de identificacion.
     * @param fechaNac   Fecha de nacimiento en formato "aaaa-MM-dd".
     * @param contrasena Contrasena de acceso.
     * @param correo     Correo electronico.
     * @param direccion  Direccion de domicilio.
     * @return Mensaje de exito o error.
     */
    public String registrarVendedor(String nombre, String id, String fechaNac,
                                    String contrasena, String correo, String direccion) {
        // Validar mayor de edad (RN7)
        Vendedor temp = new Vendedor(nombre, id, fechaNac, contrasena, correo, 0, direccion);
        if (!temp.esMayorDeEdad()) {
            return "ERROR: Debe ser mayor de edad para registrarse.";
        }
        if (buscarPorId(id) != null) {
            return "ERROR: Ya existe un usuario con ese ID.";
        }
        dao.insertar(temp);
        usuarios.add(temp);
        return "Vendedor registrado correctamente.";
    }

    /**
     * Registra un nuevo coleccionista en el sistema.
     * Valida que sea mayor de edad (RN7) y que no exista otro usuario con el mismo ID.
     *
     * @param nombre     Nombre completo del coleccionista.
     * @param id         Numero de identificacion.
     * @param fechaNac   Fecha de nacimiento en formato "aaaa-MM-dd".
     * @param contrasena Contrasena de acceso.
     * @param correo     Correo electronico.
     * @param direccion  Direccion de domicilio.
     * @return Mensaje de exito o error.
     */
    public String registrarColeccionista(String nombre, String id, String fechaNac,
                                         String contrasena, String correo, String direccion) {
        // Validar mayor de edad (RN7)
        Coleccionista temp = new Coleccionista(nombre, id, fechaNac, contrasena, correo, 0, direccion);
        if (!temp.esMayorDeEdad()) {
            return "ERROR: Debe ser mayor de edad para registrarse.";
        }
        if (buscarPorId(id) != null) {
            return "ERROR: Ya existe un usuario con ese ID.";
        }
        dao.insertar(temp);
        usuarios.add(temp);
        return "Coleccionista registrado correctamente.";
    }

    /**
     * Autentica un usuario verificando sus credenciales.
     *
     * @param id         ID del usuario.
     * @param contrasena Contrasena proporcionada.
     * @return El Usuario autenticado, o null si las credenciales son incorrectas o la cuenta esta inactiva.
     */
    public Usuario autenticar(String id, String contrasena) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) return null;
        if (!usuario.getContrasena().equals(contrasena)) return null;
        if (!usuario.isActivo()) return null;
        return usuario;
    }

    /**
     * Modifica los datos de un usuario existente.
     *
     * @param id             ID del usuario.
     * @param nuevoNombre    Nuevo nombre (null o vacio para no cambiar).
     * @param nuevoCorreo    Nuevo correo (null o vacio para no cambiar).
     * @param nuevaDireccion Nueva direccion (null o vacio para no cambiar).
     * @return Mensaje de exito o error.
     */
    public String modificarUsuario(String id, String nuevoNombre,
                                   String nuevoCorreo, String nuevaDireccion) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) {
            return "ERROR: No existe un usuario con ese ID.";
        }
        if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
            usuario.setNombreCompleto(nuevoNombre.trim());
        }
        if (nuevoCorreo != null && !nuevoCorreo.trim().isEmpty()) {
            usuario.setCorreoElectronico(nuevoCorreo.trim());
        }
        if (nuevaDireccion != null && !nuevaDireccion.trim().isEmpty()) {
            if (usuario instanceof Vendedor) {
                ((Vendedor) usuario).setDireccion(nuevaDireccion.trim());
            } else if (usuario instanceof Coleccionista) {
                ((Coleccionista) usuario).setDireccion(nuevaDireccion.trim());
            }
        }
        dao.actualizar(id,
            usuario.getNombreCompleto(),
            usuario.getCorreoElectronico(),
            (usuario instanceof Vendedor) ? ((Vendedor) usuario).getDireccion() :
            (usuario instanceof Coleccionista) ? ((Coleccionista) usuario).getDireccion() : "");
        return "Usuario modificado correctamente.";
    }

    /**
     * Activa la cuenta de un usuario.
     *
     * @param id ID del usuario.
     * @return Mensaje de exito o error.
     */
    public String activarUsuario(String id) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) return "ERROR: No existe un usuario con ese ID.";
        if (usuario.isActivo()) return "El usuario ya se encuentra activo.";
        usuario.setActivo(true);
        dao.actualizarEstado(id, true);
        return "Usuario activado correctamente.";
    }

    /**
     * Inactiva la cuenta de un usuario.
     * No se puede inactivar al moderador del sistema.
     *
     * @param id ID del usuario.
     * @return Mensaje de exito o error.
     */
    public String inactivarUsuario(String id) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) return "ERROR: No existe un usuario con ese ID.";
        if (usuario instanceof Moderador) return "ERROR: No se puede inactivar al moderador del sistema.";
        if (!usuario.isActivo()) return "El usuario ya se encuentra inactivo.";
        usuario.setActivo(false);
        dao.actualizarEstado(id, false);
        return "Usuario inactivado correctamente.";
    }

    /**
     * Designa a un coleccionista como moderador de subastas.
     *
     * @param id ID del coleccionista.
     * @return Mensaje de exito o error.
     */
    public String asignarComoModerador(String id) {
        Usuario usuario = buscarPorId(id);
        if (usuario == null) return "ERROR: No existe un usuario con ese ID.";
        if (!(usuario instanceof Coleccionista)) {
            return "ERROR: Solo los coleccionistas pueden ser designados como moderadores de subastas.";
        }
        Coleccionista col = (Coleccionista) usuario;
        if (col.isEsModerador()) return "Este coleccionista ya tiene el rol de moderador de subastas.";
        col.setEsModerador(true);
        dao.actualizarRolModerador(id, true);
        return "Coleccionista " + col.getNombreCompleto() + " designado como moderador de subastas.";
    }

    /**
     * Busca un usuario en memoria por su identificacion.
     *
     * @param id Identificacion a buscar.
     * @return El Usuario encontrado, o null si no existe.
     */
    public Usuario buscarPorId(String id) {
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getIdentificacion().equals(id)) {
                return usuarios.get(i);
            }
        }
        return null;
    }

    /**
     * Genera un listado textual de todos los usuarios registrados.
     *
     * @return Cadena con los usuarios numerados o mensaje de lista vacia.
     */
    public String listarUsuarios() {
        if (usuarios.isEmpty()) return "No hay usuarios registrados.";
        String lista = "";
        for (int i = 0; i < usuarios.size(); i++) {
            lista += (i + 1) + ". " + usuarios.get(i).toString() + "\n";
        }
        return lista;
    }

    /**
     * Retorna la lista completa de usuarios.
     *
     * @return Lista de todos los usuarios.
     */
    public ArrayList<Usuario> getUsuarios() {
        return usuarios;
    }

    /**
     * Retorna la lista de coleccionistas activos en el sistema.
     *
     * @return Lista de coleccionistas activos.
     */
    public ArrayList<Coleccionista> listarColeccionistas() {
        ArrayList<Coleccionista> lista = new ArrayList<>();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i) instanceof Coleccionista && usuarios.get(i).isActivo()) {
                lista.add((Coleccionista) usuarios.get(i));
            }
        }
        return lista;
    }

    /**
     * Retorna la lista de coleccionistas con el rol de moderador de subastas.
     *
     * @return Lista de coleccionistas moderadores.
     */
    public ArrayList<Coleccionista> listarColeccionistasModerador() {
        ArrayList<Coleccionista> resultado = new ArrayList<>();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i) instanceof Coleccionista) {
                Coleccionista c = (Coleccionista) usuarios.get(i);
                if (c.isEsModerador()) resultado.add(c);
            }
        }
        return resultado;
    }

    /**
     * Inicia sesion y retorna un mensaje de resultado.
     *
     * @param id         ID del usuario.
     * @param contrasena Contrasena.
     * @return Mensaje de exito o error.
     */
    public String iniciarSesion(String id, String contrasena) {
        Usuario u = autenticar(id, contrasena);
        if (u == null) return "ERROR: Credenciales incorrectas o cuenta inactiva.";
        return "Sesion iniciada correctamente.";
    }
}
