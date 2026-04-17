package proyecto_subastas.dominio;

import java.time.LocalDate;

/**
 * Clase que representa una oferta economica presentada por un coleccionista en una subasta.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class Oferta {

    /** Nombre completo del coleccionista que realiza la oferta. */
    private String nombreOferente;

    /** Puntuacion de reputacion del oferente en el momento de la oferta. */
    private double puntuacionOferente;

    /** Precio economico ofertado por los objetos de la subasta. */
    private double precioOfertado;

    /** Fecha en la que se realizo la oferta. */
    private LocalDate fechaOferta;

    /** Constructor por defecto. */
    public Oferta() {
        this.fechaOferta = LocalDate.now();
    }

    /**
     * Constructor completo que inicializa todos los atributos de la oferta.
     *
     * @param nombreOferente      Nombre del coleccionista que oferta.
     * @param puntuacionOferente  Puntuacion de reputacion del oferente.
     * @param precioOfertado      Monto economico de la oferta.
     */
    public Oferta(String nombreOferente, double puntuacionOferente, double precioOfertado) {
        this.nombreOferente = nombreOferente;
        this.puntuacionOferente = puntuacionOferente;
        this.precioOfertado = precioOfertado;
        this.fechaOferta = LocalDate.now();
    }

    // Getters / Setters

    public String getNombreOferente() { return nombreOferente; }
    public void setNombreOferente(String nombreOferente) { this.nombreOferente = nombreOferente; }

    public double getPuntuacionOferente() { return puntuacionOferente; }
    public void setPuntuacionOferente(double puntuacionOferente) { this.puntuacionOferente = puntuacionOferente; }

    public double getPrecioOfertado() { return precioOfertado; }
    public void setPrecioOfertado(double precioOfertado) { this.precioOfertado = precioOfertado; }

    public LocalDate getFechaOferta() { return fechaOferta; }
    public void setFechaOferta(LocalDate fechaOferta) { this.fechaOferta = fechaOferta; }

    /**
     * Dos ofertas son iguales si el mismo oferente propone el mismo precio.
     *
     * @param obj Objeto a comparar.
     * @return true si nombre del oferente y precio coinciden.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Oferta)) return false;
        Oferta otra = (Oferta) obj;
        return Double.compare(this.precioOfertado, otra.precioOfertado) == 0
                && this.nombreOferente != null && this.nombreOferente.equals(otra.nombreOferente);
    }

    /**
     * Retorna una representacion textual de la oferta con sus datos principales.
     *
     * @return Cadena con nombre del oferente, puntuacion, monto ofertado y fecha.
     */
    @Override
    public String toString() {
        return "Oferente: " + nombreOferente
                + " | Puntuacion: " + puntuacionOferente
                + " | Monto: $" + precioOfertado
                + " | Fecha: " + fechaOferta;
    }
}
