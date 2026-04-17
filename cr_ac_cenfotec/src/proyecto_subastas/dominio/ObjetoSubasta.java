package proyecto_subastas.dominio;

import java.time.LocalDate;
import java.time.Period;

/**
 * Clase que representa un objeto ofrecido dentro de una subasta en la plataforma.
 * Cada objeto tiene nombre, descripcion, estado de conservacion y fecha de compra.
 * El sistema calcula automaticamente la antiguedad del objeto en anos, meses y dias,
 * tal como lo exige la consigna del proyecto.
 *
 * Los estados posibles de un objeto son:
 * - Nuevo: Objeto sin uso.
 * - Usado: Objeto que ha sido utilizado.
 * - Antiguo sin abrir: Objeto antiguo que no ha sido abierto o usado.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class ObjetoSubasta {

    /** Nombre descriptivo del objeto. */
    private String nombre;

    /** Descripcion detallada del objeto. */
    private String descripcion;

    /** Estado de conservacion del objeto: "Nuevo", "Usado" o "Antiguo sin abrir". */
    private String estado;

    /**
     * Fecha de compra del objeto en formato "aaaa-MM-dd".
     * Se usa para calcular la antiguedad exacta en anos, meses y dias.
     * Ejemplo: "2010-03-20"
     */
    private String fechaCompra;

    /** Constructor por defecto. */
    public ObjetoSubasta() {}

    /**
     * Constructor completo que inicializa todos los atributos del objeto.
     *
     * @param nombre      Nombre del objeto.
     * @param descripcion Descripcion detallada del objeto.
     * @param estado      Estado de conservacion ("Nuevo", "Usado", "Antiguo sin abrir").
     * @param fechaCompra Fecha en que fue adquirido en formato "aaaa-MM-dd".
     */
    public ObjetoSubasta(String nombre, String descripcion, String estado, String fechaCompra) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.estado = estado;
        this.fechaCompra = fechaCompra;
    }

    /**
     * Calcula la antiguedad aproximada del objeto en anos completos.
     * Metodo mantenido por compatibilidad con versiones anteriores.
     *
     * @return Antiguedad del objeto en anos completos.
     */
    public int calcularAntiguedad() {
        try {
            LocalDate compra = LocalDate.parse(fechaCompra);
            return Period.between(compra, LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Calcula la antiguedad exacta del objeto en anos, meses y dias.
     * Cumple con el requisito de la consigna: "Antiguedad calculada (anos, meses y dias)".
     * Se asume la fecha de compra indicada en el campo fechaCompra.
     *
     * @return Cadena formateada con la antiguedad exacta: "X anno(s), Y mes(es), Z dia(s)".
     */
    public String calcularAntiguedadDetallada() {
        try {
            LocalDate compra = LocalDate.parse(fechaCompra);
            Period periodo = Period.between(compra, LocalDate.now());
            return periodo.getYears() + " anno(s), "
                    + periodo.getMonths() + " mes(es), "
                    + periodo.getDays() + " dia(s)";
        } catch (Exception e) {
            return "Fecha de compra invalida";
        }
    }

    // Getters / Setters

    /** @return Nombre descriptivo del objeto. */
    public String getNombre() { return nombre; }
    /** @param nombre Nuevo nombre del objeto. */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /** @return Descripcion detallada del objeto. */
    public String getDescripcion() { return descripcion; }
    /** @param descripcion Nueva descripcion del objeto. */
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /** @return Estado de conservacion del objeto. */
    public String getEstado() { return estado; }
    /** @param estado Nuevo estado del objeto. */
    public void setEstado(String estado) { this.estado = estado; }

    /** @return Fecha de compra en formato "aaaa-MM-dd". */
    public String getFechaCompra() { return fechaCompra; }
    /** @param fechaCompra Nueva fecha de compra en formato "aaaa-MM-dd". */
    public void setFechaCompra(String fechaCompra) { this.fechaCompra = fechaCompra; }

    /**
     * Dos objetos son iguales si tienen el mismo nombre y fecha de compra.
     *
     * @param obj Objeto a comparar.
     * @return true si nombre y fecha de compra coinciden.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof ObjetoSubasta)) return false;
        ObjetoSubasta otro = (ObjetoSubasta) obj;
        return this.fechaCompra != null && this.fechaCompra.equals(otro.fechaCompra)
                && this.nombre != null && this.nombre.equals(otro.nombre);
    }

    /**
     * Retorna una representacion textual del objeto con su antiguedad en
     * anos, meses y dias tal como exige la consigna.
     *
     * @return Cadena con nombre, estado y antiguedad exacta.
     */
    @Override
    public String toString() {
        return "Objeto: " + nombre
                + " | Estado: " + estado
                + " | Antiguedad: " + calcularAntiguedadDetallada();
    }
}
