package proyecto_subastas.dominio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * Clase que representa una subasta publicada en la plataforma de coleccionistas.
 * Una subasta agrupa uno o mas objetos ofrecidos por un usuario creador,
 * con un precio minimo de aceptacion y una fecha de vencimiento.
 *
 * Campos requeridos por la consigna:
 * - Fecha de vencimiento
 * - Tiempo restante para vencer (dias, horas, minutos y segundos)
 * - Usuario creador de la subasta
 * - Puntuacion del creador
 * - Precio minimo de aceptacion
 * - Conjunto de objetos subastados
 * - Estado de la subasta
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 * @see ObjetoSubasta
 * @see Oferta
 * @see Usuario
 */
public class Subasta {

    /** Contador estatico para asignar identificadores unicos a cada subasta. */
    private static int contadorId = 1;

    /** Identificador unico de la subasta. */
    private int id;

    /** Usuario (vendedor o coleccionista) que creo la subasta. */
    private Usuario creador;

    /**
     * Puntuacion del creador al momento de registrar la subasta.
     * Campo requerido explicitamente por la consigna como atributo de Subasta.
     */
    private double puntuacionCreador;

    /** Lista de objetos incluidos en la subasta. */
    private ArrayList<ObjetoSubasta> objetos;

    /** Lista de ofertas registradas en la subasta. */
    private ArrayList<Oferta> ofertas;

    /** Precio minimo que debe superar cualquier oferta para ser valida. */
    private double precioMinimo;

    /** Estado actual de la subasta: "Activa", "Cerrada", "Adjudicada" o "Completada". */
    private String estado;

    /** Fecha de vencimiento de la subasta en formato "aaaa-MM-dd". */
    private String fechaVencimiento;

    /** Coleccionista designado como moderador de esta subasta. */
    private Coleccionista moderadorAsignado;

    /** Oferta ganadora seleccionada al cierre de la subasta. */
    private Oferta ofertaGanadora;

    /** Orden de adjudicacion generada al aceptar el ganador. */
    private OrdenAdjudicacion orden;

    /** Indica si el ganador acepto la adjudicacion. */
    private boolean adjudicacionAceptada;

    /** Indica si el ganador confirmo la recepcion de los objetos. */
    private boolean entregaConfirmada;

    /** Calificacion dada por el ganador al vendedor (1-5). */
    private int calificacionGanador;

    /** Calificacion dada por el vendedor al ganador (1-5). */
    private int calificacionVendedor;

    /**
     * Constructor por defecto.
     * Crea una subasta con ID autoincremental, listas vacias y estado "Activa".
     */
    public Subasta() {
        this.id = contadorId++;
        this.objetos = new ArrayList<>();
        this.ofertas = new ArrayList<>();
        this.estado = "Activa";
        this.adjudicacionAceptada = false;
        this.entregaConfirmada = false;
        this.calificacionGanador = 0;
        this.calificacionVendedor = 0;
        this.puntuacionCreador = 0;
    }

    /**
     * Constructor completo que inicializa todos los atributos de la subasta.
     * Extrae automaticamente la puntuacion del creador segun su tipo de usuario.
     *
     * @param creador          Usuario que crea la subasta (vendedor o coleccionista).
     * @param objetos          Lista de objetos ofrecidos en la subasta.
     * @param precioMinimo     Precio minimo de aceptacion de ofertas.
     * @param fechaVencimiento Fecha de vencimiento en formato "aaaa-MM-dd".
     */
    public Subasta(Usuario creador, ArrayList<ObjetoSubasta> objetos,
                   double precioMinimo, String fechaVencimiento) {
        this.id = contadorId++;
        this.creador = creador;
        this.objetos = objetos;
        this.ofertas = new ArrayList<>();
        this.precioMinimo = precioMinimo;
        this.estado = "Activa";
        this.fechaVencimiento = fechaVencimiento;
        this.adjudicacionAceptada = false;
        this.entregaConfirmada = false;
        this.calificacionGanador = 0;
        this.calificacionVendedor = 0;

        // Extraer puntuacion del creador segun su tipo (campo requerido por consigna)
        if (creador instanceof Vendedor) {
            this.puntuacionCreador = ((Vendedor) creador).getPuntuacion();
        } else if (creador instanceof Coleccionista) {
            this.puntuacionCreador = ((Coleccionista) creador).getPuntuacion();
        } else {
            this.puntuacionCreador = 0;
        }
    }

    /**
     * Calcula el tiempo restante hasta la fecha de vencimiento.
     * Retorna dias, horas, minutos y segundos tal como exige la consigna:
     * "Tiempo restante para vencer (dias, horas, minutos y segundos)".
     *
     * @return Cadena con el tiempo restante en formato "Xd Xh Xm Xs", o "VENCIDA" si ya expiro.
     */
    public String calcularTiempoRestante() {
        try {
            LocalDate fechaVenc = LocalDate.parse(fechaVencimiento);
            LocalDateTime vencimiento = fechaVenc.atTime(23, 59, 59);
            LocalDateTime ahora = LocalDateTime.now();

            if (ahora.isAfter(vencimiento)) {
                return "VENCIDA";
            }

            long totalSegundos = ChronoUnit.SECONDS.between(ahora, vencimiento);
            long dias     = totalSegundos / 86400;
            long horas    = (totalSegundos % 86400) / 3600;
            long minutos  = (totalSegundos % 3600) / 60;
            long segundos = totalSegundos % 60;

            return dias + "d " + horas + "h " + minutos + "m " + segundos + "s";
        } catch (Exception e) {
            return "Fecha invalida";
        }
    }

    /**
     * Retorna el monto de la oferta mas alta registrada en la subasta.
     *
     * @return Monto maximo ofertado, o 0 si no hay ofertas.
     */
    public double getOfertaMayor() {
        double max = 0;
        for (int i = 0; i < ofertas.size(); i++) {
            if (ofertas.get(i).getPrecioOfertado() > max) {
                max = ofertas.get(i).getPrecioOfertado();
            }
        }
        return max;
    }

    // Getters / Setters

    /** @return Identificador unico de la subasta. */
    public int getId() { return id; }

    /** @return Usuario creador de la subasta. */
    public Usuario getCreador() { return creador; }
    /** @param creador Nuevo creador. */
    public void setCreador(Usuario creador) { this.creador = creador; }

    /** @return Puntuacion del creador al momento de registrar la subasta. */
    public double getPuntuacionCreador() { return puntuacionCreador; }
    /** @param puntuacionCreador Nueva puntuacion del creador. */
    public void setPuntuacionCreador(double puntuacionCreador) { this.puntuacionCreador = puntuacionCreador; }

    /** @return Lista de objetos subastados. */
    public ArrayList<ObjetoSubasta> getObjetos() { return objetos; }
    /** @param objetos Nueva lista de objetos. */
    public void setObjetos(ArrayList<ObjetoSubasta> objetos) { this.objetos = objetos; }

    /** @return Lista de ofertas registradas. */
    public ArrayList<Oferta> getOfertas() { return ofertas; }
    /** @param ofertas Nueva lista de ofertas. */
    public void setOfertas(ArrayList<Oferta> ofertas) { this.ofertas = ofertas; }

    /** @return Precio minimo de aceptacion. */
    public double getPrecioMinimo() { return precioMinimo; }
    /** @param precioMinimo Nuevo precio minimo. */
    public void setPrecioMinimo(double precioMinimo) { this.precioMinimo = precioMinimo; }

    /** @return Estado actual de la subasta. */
    public String getEstado() { return estado; }
    /** @param estado Nuevo estado. */
    public void setEstado(String estado) { this.estado = estado; }

    /** @return Fecha de vencimiento en formato "aaaa-MM-dd". */
    public String getFechaVencimiento() { return fechaVencimiento; }
    /** @param fechaVencimiento Nueva fecha de vencimiento. */
    public void setFechaVencimiento(String fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    /** @return Moderador asignado a esta subasta. */
    public Coleccionista getModeradorAsignado() { return moderadorAsignado; }
    /** @param moderadorAsignado Nuevo moderador asignado. */
    public void setModeradorAsignado(Coleccionista moderadorAsignado) { this.moderadorAsignado = moderadorAsignado; }

    /** @return Oferta ganadora de la subasta. */
    public Oferta getOfertaGanadora() { return ofertaGanadora; }
    /** @param ofertaGanadora Nueva oferta ganadora. */
    public void setOfertaGanadora(Oferta ofertaGanadora) { this.ofertaGanadora = ofertaGanadora; }

    /** @return Orden de adjudicacion generada. */
    public OrdenAdjudicacion getOrden() { return orden; }
    /** @param orden Nueva orden de adjudicacion. */
    public void setOrden(OrdenAdjudicacion orden) { this.orden = orden; }

    /** @return true si el ganador acepto la adjudicacion. */
    public boolean isAdjudicacionAceptada() { return adjudicacionAceptada; }
    /** @param adjudicacionAceptada true si acepto. */
    public void setAdjudicacionAceptada(boolean adjudicacionAceptada) { this.adjudicacionAceptada = adjudicacionAceptada; }

    /** @return true si el ganador confirmo la entrega. */
    public boolean isEntregaConfirmada() { return entregaConfirmada; }
    /** @param entregaConfirmada true si confirmo. */
    public void setEntregaConfirmada(boolean entregaConfirmada) { this.entregaConfirmada = entregaConfirmada; }

    /** @return Calificacion del ganador al vendedor (1-5). */
    public int getCalificacionGanador() { return calificacionGanador; }
    /** @param calificacionGanador Nueva calificacion. */
    public void setCalificacionGanador(int calificacionGanador) { this.calificacionGanador = calificacionGanador; }

    /** @return Calificacion del vendedor al ganador (1-5). */
    public int getCalificacionVendedor() { return calificacionVendedor; }
    /** @param calificacionVendedor Nueva calificacion. */
    public void setCalificacionVendedor(int calificacionVendedor) { this.calificacionVendedor = calificacionVendedor; }

    /**
     * Dos subastas son iguales si tienen el mismo ID.
     *
     * @param obj Objeto a comparar.
     * @return true si el ID coincide.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Subasta)) return false;
        Subasta otra = (Subasta) obj;
        return this.id == otra.id;
    }

    /**
     * Retorna una representacion textual completa de la subasta.
     * Incluye todos los campos requeridos por la consigna: tiempo restante
     * en dias/horas/minutos/segundos, puntuacion del creador, cantidad de
     * ofertas y oferta mayor actual.
     *
     * @return Cadena con todos los datos relevantes de la subasta.
     */
    @Override
    public String toString() {
        String mod = moderadorAsignado != null
                ? moderadorAsignado.getNombreCompleto() : "Sin asignar";
        return "Subasta #" + id
                + " | Creador: " + creador.getNombreCompleto()
                + " | Puntuacion creador: " + puntuacionCreador
                + " | Precio min: $" + precioMinimo
                + " | Estado: " + estado
                + " | Vence: " + fechaVencimiento
                + " | Tiempo restante: " + calcularTiempoRestante()
                + " | Moderador: " + mod
                + " | Objetos: " + objetos.size()
                + " | Ofertas: " + ofertas.size()
                + " | Oferta mayor: $" + getOfertaMayor();
    }
}
