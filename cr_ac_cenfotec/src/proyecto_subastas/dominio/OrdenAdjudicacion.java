package proyecto_subastas.dominio;

import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Clase que representa la orden de adjudicacion generada al finalizar una subasta.
 * Una orden es la aceptacion formal de la adjudicacion de una subasta por parte del ganador.
 * Contiene el nombre del ganador, la fecha en que se genero la orden,
 * el detalle de los objetos adjudicados y el precio total pagado.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class OrdenAdjudicacion {

    /** Nombre completo del coleccionista ganador de la subasta. */
    private String nombreGanador;

    /** Fecha en la que se genero la orden de adjudicacion. */
    private LocalDate fechaOrden;

    /** Lista de objetos adjudicados al ganador. */
    private ArrayList<ObjetoSubasta> objetosAdjudicados;

    /** Precio total pagado por el ganador (monto de la oferta ganadora). */
    private double precioTotal;

    /** Constructor por defecto. Inicializa la lista de objetos vacia y la fecha como hoy. */
    public OrdenAdjudicacion() {
        this.fechaOrden = LocalDate.now();
        this.objetosAdjudicados = new ArrayList<>();
    }

    /**
     * Constructor completo que inicializa todos los atributos de la orden.
     *
     * @param nombreGanador       Nombre completo del ganador.
     * @param objetosAdjudicados  Lista de objetos que le corresponden al ganador.
     * @param precioTotal         Precio total pagado (monto de la oferta ganadora).
     */
    public OrdenAdjudicacion(String nombreGanador,
                              ArrayList<ObjetoSubasta> objetosAdjudicados,
                              double precioTotal) {
        this.nombreGanador = nombreGanador;
        this.fechaOrden = LocalDate.now();
        this.objetosAdjudicados = objetosAdjudicados;
        this.precioTotal = precioTotal;
    }

    // Getters / Setters

    public String getNombreGanador() { return nombreGanador; }
    public void setNombreGanador(String nombreGanador) { this.nombreGanador = nombreGanador; }

    public LocalDate getFechaOrden() { return fechaOrden; }
    public void setFechaOrden(LocalDate fechaOrden) { this.fechaOrden = fechaOrden; }

    public ArrayList<ObjetoSubasta> getObjetosAdjudicados() { return objetosAdjudicados; }
    public void setObjetosAdjudicados(ArrayList<ObjetoSubasta> objetosAdjudicados) {
        this.objetosAdjudicados = objetosAdjudicados;
    }

    public double getPrecioTotal() { return precioTotal; }
    public void setPrecioTotal(double precioTotal) { this.precioTotal = precioTotal; }

    /**
     * Dos ordenes son iguales si tienen el mismo ganador y precio total.
     *
     * @param obj Objeto a comparar.
     * @return true si nombre del ganador y precio total coinciden.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof OrdenAdjudicacion)) return false;
        OrdenAdjudicacion otra = (OrdenAdjudicacion) obj;
        return Double.compare(this.precioTotal, otra.precioTotal) == 0
                && this.nombreGanador != null && this.nombreGanador.equals(otra.nombreGanador);
    }

    /**
     * Retorna una representacion textual de la orden con todos sus datos.
     *
     * @return Cadena con ganador, fecha, lista de objetos y precio total.
     */
    @Override
    public String toString() {
        String resultado = "=== ORDEN DE ADJUDICACION ===\n";
        resultado += "Ganador     : " + nombreGanador + "\n";
        resultado += "Fecha orden : " + fechaOrden + "\n";
        resultado += "Objetos     :\n";
        for (int i = 0; i < objetosAdjudicados.size(); i++) {
            resultado += "  " + (i + 1) + ". " + objetosAdjudicados.get(i).toString() + "\n";
        }
        resultado += "Precio total: $" + precioTotal;
        return resultado;
    }
}
