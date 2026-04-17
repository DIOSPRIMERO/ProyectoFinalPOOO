package proyecto_subastas.dominio;

import java.time.LocalDate;
import java.time.Period;

/**
 * Clase abstracta que representa un usuario generico dentro de la plataforma de subastas.
 * Todo usuario posee datos basicos de identificacion y acceso.
 * Las subclases concretas (Moderador, Vendedor, Coleccionista) amplian esta clase
 * con atributos y comportamientos especificos segun su rol.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public abstract class Usuario {

    /** Nombre completo del usuario. */
    private String nombreCompleto;

    /** Numero de identificacion unico del usuario. */
    private String identificacion;

    /**
     * Fecha de nacimiento del usuario en formato "aaaa-MM-dd".
     * Se utiliza para calcular la edad exacta en anos completos.
     * Ejemplo: "1990-05-15"
     */
    private String fechaNacimiento;

    /** Contrasena de acceso a la plataforma. */
    private String contrasena;

    /** Correo electronico del usuario. */
    private String correoElectronico;

    /** Indica si la cuenta del usuario esta activa en la plataforma. */
    private boolean activo;

    /** Constructor por defecto. Inicializa activo en true. */
    public Usuario() {
        this.activo = true;
    }

    /**
     * Constructor completo que inicializa todos los atributos del usuario.
     *
     * @param nombreCompleto    Nombre completo del usuario.
     * @param identificacion    Numero de identificacion del usuario.
     * @param fechaNacimiento   Fecha de nacimiento en formato "aaaa-MM-dd" (ej: "1990-05-15").
     * @param contrasena        Contrasena de acceso.
     * @param correoElectronico Correo electronico del usuario.
     */
    public Usuario(String nombreCompleto, String identificacion,
                   String fechaNacimiento, String contrasena, String correoElectronico) {
        this.nombreCompleto = nombreCompleto;
        this.identificacion = identificacion;
        this.fechaNacimiento = fechaNacimiento;
        this.contrasena = contrasena;
        this.correoElectronico = correoElectronico;
        this.activo = true;
    }

    /**
     * Calcula la edad exacta del usuario en anos completos a partir de su
     * fecha de nacimiento y la fecha actual del sistema.
     * Cumple con el requisito de la consigna: "Edad calculada a partir de la fecha de nacimiento".
     *
     * @return Edad del usuario en anos completos, o 0 si la fecha no es valida.
     */
    public int calcularEdad() {
        try {
            LocalDate nacimiento = LocalDate.parse(fechaNacimiento);
            return Period.between(nacimiento, LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Verifica si el usuario es mayor de edad (18 anos o mas) segun su fecha de nacimiento.
     * Usado por los controladores para validar registros.
     *
     * @return true si el usuario tiene 18 anos o mas.
     */
    public boolean esMayorDeEdad() {
        return calcularEdad() >= 18;
    }

    // Getters / Setters

    /** @return Nombre completo del usuario. */
    public String getNombreCompleto() { return nombreCompleto; }
    /** @param nombreCompleto Nuevo nombre completo. */
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    /** @return Numero de identificacion del usuario. */
    public String getIdentificacion() { return identificacion; }
    /** @param identificacion Nuevo numero de identificacion. */
    public void setIdentificacion(String identificacion) { this.identificacion = identificacion; }

    /** @return Fecha de nacimiento en formato "aaaa-MM-dd". */
    public String getFechaNacimiento() { return fechaNacimiento; }
    /** @param fechaNacimiento Nueva fecha de nacimiento en formato "aaaa-MM-dd". */
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    /** @return Contrasena de acceso del usuario. */
    public String getContrasena() { return contrasena; }
    /** @param contrasena Nueva contrasena. */
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    /** @return Correo electronico del usuario. */
    public String getCorreoElectronico() { return correoElectronico; }
    /** @param correoElectronico Nuevo correo electronico. */
    public void setCorreoElectronico(String correoElectronico) { this.correoElectronico = correoElectronico; }

    /** @return true si la cuenta esta activa. */
    public boolean isActivo() { return activo; }
    /** @param activo true para activar, false para inactivar. */
    public void setActivo(boolean activo) { this.activo = activo; }

    /**
     * Dos usuarios son iguales si comparten el mismo numero de identificacion.
     *
     * @param obj Objeto a comparar.
     * @return true si la identificacion coincide; false en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Usuario)) return false;
        Usuario otro = (Usuario) obj;
        return this.identificacion != null && this.identificacion.equals(otro.identificacion);
    }

    /**
     * Retorna una representacion textual del usuario con su edad calculada
     * a partir de la fecha de nacimiento real.
     *
     * @return Cadena con nombre, identificacion, edad, nacimiento, correo y estado.
     */
    @Override
    public String toString() {
        return "Nombre: " + nombreCompleto
                + " | ID: " + identificacion
                + " | Edad: " + calcularEdad() + " annos"
                + " | Nacimiento: " + fechaNacimiento
                + " | Correo: " + correoElectronico
                + " | Estado: " + (activo ? "Activo" : "Inactivo");
    }
}
