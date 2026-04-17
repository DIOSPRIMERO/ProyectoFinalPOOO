package proyecto_subastas.dominio;

/**
 * Clase que representa a un vendedor registrado en la plataforma de subastas.
 * El vendedor puede crear subastas con sus objetos, pero no puede realizar ofertas.
 * Debe ser mayor de edad para registrarse en el sistema.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 * @see Usuario
 */
public class Vendedor extends Usuario {

    /** Puntuacion de reputacion del vendedor dentro de la plataforma. */
    private double puntuacion;

    /** Direccion de domicilio del vendedor. */
    private String direccion;

    /**
     * Constructor por defecto.
     * Crea una instancia de Vendedor con puntuacion inicial en 0.
     */
    public Vendedor() {
        this.puntuacion = 0;
    }

    /**
     * Constructor completo que inicializa todos los atributos del vendedor.
     *
     * @param nombreCompleto    Nombre completo del vendedor.
     * @param identificacion    Numero de identificacion del vendedor.
     * @param fechaNacimiento   Fecha de nacimiento en formato "aaaa-MM-dd".
     * @param contrasena        Contrasena de acceso a la plataforma.
     * @param correoElectronico Correo electronico del vendedor.
     * @param puntuacion        Puntuacion de reputacion inicial del vendedor.
     * @param direccion         Direccion de domicilio del vendedor.
     */
    public Vendedor(String nombreCompleto, String identificacion,
                    String fechaNacimiento, String contrasena, String correoElectronico,
                    double puntuacion, String direccion) {
        super(nombreCompleto, identificacion, fechaNacimiento, contrasena, correoElectronico);
        this.puntuacion = puntuacion;
        this.direccion = direccion;
    }

    // Getters / Setters

    /** @return Puntuacion de reputacion del vendedor. */
    public double getPuntuacion() { return puntuacion; }
    /** @param puntuacion Nueva puntuacion. */
    public void setPuntuacion(double puntuacion) { this.puntuacion = puntuacion; }

    /** @return Direccion de domicilio del vendedor. */
    public String getDireccion() { return direccion; }
    /** @param direccion Nueva direccion. */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /**
     * Dos vendedores son iguales si comparten la misma identificacion.
     *
     * @param obj Objeto a comparar.
     * @return true si la identificacion coincide.
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Retorna una representacion textual del vendedor con sus datos completos.
     *
     * @return Cadena con el prefijo [VENDEDOR], datos del usuario, puntuacion y direccion.
     */
    @Override
    public String toString() {
        return "[VENDEDOR] " + super.toString()
                + " | Puntuacion: " + puntuacion
                + " | Direccion: " + direccion;
    }
}
