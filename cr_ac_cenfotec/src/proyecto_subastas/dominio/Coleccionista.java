package proyecto_subastas.dominio;

import java.util.ArrayList;

/**
 * Clase que representa a un coleccionista registrado en la plataforma de subastas.
 * El coleccionista es el unico tipo de usuario que puede realizar ofertas en subastas.
 * Tambien puede crear subastas, pero unicamente con objetos que esten registrados
 * en su coleccion personal. No puede ofertar en subastas que el mismo haya creado.
 * Puede ser designado como moderador de subastas por el administrador del sistema.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 * @see Usuario
 * @see ObjetoSubasta
 */
public class Coleccionista extends Usuario {

    /** Puntuacion de reputacion del coleccionista dentro de la plataforma. */
    private double puntuacion;

    /** Direccion de domicilio del coleccionista. */
    private String direccion;

    /** Lista de categorias o temas de interes del coleccionista. */
    private ArrayList<String> intereses;

    /** Coleccion de objetos que pertenecen al coleccionista. */
    private ArrayList<ObjetoSubasta> coleccion;

    /**
     * Indica si este coleccionista ha sido designado como moderador de subastas
     * por el administrador del sistema.
     */
    private boolean esModerador;

    /**
     * Constructor por defecto.
     * Crea una instancia de Coleccionista con puntuacion en 0 y listas vacias.
     */
    public Coleccionista() {
        this.puntuacion = 0;
        this.intereses = new ArrayList<>();
        this.coleccion = new ArrayList<>();
        this.esModerador = false;
    }

    /**
     * Constructor completo que inicializa todos los atributos del coleccionista.
     *
     * @param nombreCompleto    Nombre completo del coleccionista.
     * @param identificacion    Numero de identificacion del coleccionista.
     * @param fechaNacimiento   Fecha de nacimiento en formato "aaaa-MM-dd".
     * @param contrasena        Contrasena de acceso a la plataforma.
     * @param correoElectronico Correo electronico del coleccionista.
     * @param puntuacion        Puntuacion de reputacion inicial.
     * @param direccion         Direccion de domicilio del coleccionista.
     */
    public Coleccionista(String nombreCompleto, String identificacion,
                         String fechaNacimiento, String contrasena, String correoElectronico,
                         double puntuacion, String direccion) {
        super(nombreCompleto, identificacion, fechaNacimiento, contrasena, correoElectronico);
        this.puntuacion = puntuacion;
        this.direccion = direccion;
        this.intereses = new ArrayList<>();
        this.coleccion = new ArrayList<>();
        this.esModerador = false;
    }

    // Getters / Setters

    /** @return Puntuacion de reputacion del coleccionista. */
    public double getPuntuacion() { return puntuacion; }
    /** @param puntuacion Nueva puntuacion. */
    public void setPuntuacion(double puntuacion) { this.puntuacion = puntuacion; }

    /** @return Direccion de domicilio del coleccionista. */
    public String getDireccion() { return direccion; }
    /** @param direccion Nueva direccion. */
    public void setDireccion(String direccion) { this.direccion = direccion; }

    /** @return Lista de intereses del coleccionista. */
    public ArrayList<String> getIntereses() { return intereses; }
    /** @param intereses Nueva lista de intereses. */
    public void setIntereses(ArrayList<String> intereses) { this.intereses = intereses; }

    /** @return Coleccion personal de objetos del coleccionista. */
    public ArrayList<ObjetoSubasta> getColeccion() { return coleccion; }
    /** @param coleccion Nueva coleccion de objetos. */
    public void setColeccion(ArrayList<ObjetoSubasta> coleccion) { this.coleccion = coleccion; }

    /** @return true si el coleccionista tiene el rol de moderador de subastas. */
    public boolean isEsModerador() { return esModerador; }
    /** @param esModerador true para asignar el rol, false para quitarlo. */
    public void setEsModerador(boolean esModerador) { this.esModerador = esModerador; }

    /**
     * Agrega un objeto a la coleccion personal del coleccionista.
     *
     * @param objeto Objeto a agregar a la coleccion.
     */
    public void agregarAColeccion(ObjetoSubasta objeto) {
        this.coleccion.add(objeto);
    }

    /**
     * Dos coleccionistas son iguales si comparten la misma identificacion.
     *
     * @param obj Objeto a comparar.
     * @return true si la identificacion coincide.
     */
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    /**
     * Retorna una representacion textual del coleccionista con sus datos completos.
     *
     * @return Cadena con el prefijo [COLECCIONISTA], datos del usuario, puntuacion y direccion.
     */
    @Override
    public String toString() {
        String rol = esModerador ? " [MODERADOR DE SUBASTAS]" : "";
        return "[COLECCIONISTA]" + rol + " " + super.toString()
                + " | Puntuacion: " + puntuacion
                + " | Direccion: " + direccion;
    }
}
