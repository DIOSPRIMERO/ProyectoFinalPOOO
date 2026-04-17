package proyecto_subastas.dominio;

/**
 * Clase que representa al moderador de la plataforma de subastas.
 * Solo puede existir un unico moderador registrado en el sistema.
 * Por las reglas de negocio, el moderador no puede crear subastas
 * ni realizar ofertas en la plataforma.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 * @see Usuario
 */
public class Moderador extends Usuario {

    /** Constructor por defecto. */
    public Moderador() {}

    /**
     * Constructor completo que inicializa todos los atributos del moderador.
     *
     * @param nombreCompleto    Nombre completo del moderador.
     * @param identificacion    Numero de identificacion del moderador.
     * @param fechaNacimiento   Fecha de nacimiento en formato "aaaa-MM-dd".
     * @param contrasena        Contrasena de acceso a la plataforma.
     * @param correoElectronico Correo electronico del moderador.
     */
    public Moderador(String nombreCompleto, String identificacion,
                     String fechaNacimiento, String contrasena, String correoElectronico) {
        super(nombreCompleto, identificacion, fechaNacimiento, contrasena, correoElectronico);
    }

    /**
     * Dos moderadores son iguales si comparten la misma identificacion.
     *
     * @param obj Objeto a comparar.
     * @return true si la identificacion coincide.
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Retorna una representacion textual del moderador.
     *
     * @return Cadena con el prefijo [MODERADOR] seguido de los datos del usuario.
     */
    @Override
    public String toString() {
        return "[MODERADOR] " + super.toString();
    }
}
