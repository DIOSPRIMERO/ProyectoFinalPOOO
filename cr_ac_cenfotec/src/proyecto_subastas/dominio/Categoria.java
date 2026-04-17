package proyecto_subastas.dominio;

/**
 * Clase que representa una categoria de objetos dentro de la plataforma.
 * Las categorias permiten clasificar los objetos subastados por tipo o tema.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class Categoria {

    /** Contador estatico para asignar identificadores unicos. */
    private static int contadorId = 1;

    /** Identificador unico de la categoria. */
    private int id;

    /** Nombre descriptivo de la categoria. */
    private String nombre;

    /** Descripcion de que tipo de objetos pertenecen a esta categoria. */
    private String descripcion;

    /** Constructor por defecto. */
    public Categoria() {
        this.id = contadorId++;
    }

    /**
     * Constructor completo que inicializa la categoria con nombre y descripcion.
     *
     * @param nombre      Nombre de la categoria.
     * @param descripcion Descripcion de la categoria.
     */
    public Categoria(String nombre, String descripcion) {
        this.id = contadorId++;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    // Getters / Setters

    public int getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    /**
     * Dos categorias son iguales si tienen el mismo id.
     *
     * @param obj Objeto a comparar.
     * @return true si el id coincide.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Categoria)) return false;
        Categoria otra = (Categoria) obj;
        return this.id == otra.id;
    }

    /**
     * Retorna una representacion textual de la categoria.
     *
     * @return Cadena con id, nombre y descripcion.
     */
    @Override
    public String toString() {
        return "Categoria #" + id + " | Nombre: " + nombre + " | Descripcion: " + descripcion;
    }
}
