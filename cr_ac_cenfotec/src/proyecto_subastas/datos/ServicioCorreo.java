package proyecto_subastas.datos;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Clase que simula el envio de correos electronicos dentro de la plataforma.
 *
 * La consigna establece: "el sistema debera enviar un correo electronico al moderador,
 * indicandole que ha sido seleccionado para moderar la subasta."
 *
 * Esta implementacion registra los correos en un log interno y los imprime en consola.
 * En un entorno de produccion real se integraria con JavaMail o un servicio SMTP externo.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class ServicioCorreo {

    /** Historial de correos enviados durante la sesion activa. */
    private static ArrayList<String> logCorreos = new ArrayList<>();

    /**
     * Simula el envio de un correo electronico al moderador asignado a una subasta.
     * Registra el correo en el log interno y lo imprime en consola para evidencia.
     * Cumple con la regla del flujo: "el sistema debera enviar un correo al moderador".
     *
     * @param correoModerador  Direccion de correo del moderador.
     * @param nombreModerador  Nombre completo del moderador.
     * @param idSubasta        ID de la subasta asignada al moderador.
     * @param fechaVencimiento Fecha de vencimiento de la subasta.
     * @return Mensaje confirmando el envio simulado del correo.
     */
    public static String notificarModeradorSubasta(String correoModerador,
                                                    String nombreModerador,
                                                    int idSubasta,
                                                    String fechaVencimiento) {
        String asunto = "Ha sido seleccionado como moderador - Subasta #" + idSubasta;

        String cuerpo = "Estimado/a " + nombreModerador + ",\n\n"
                + "Le comunicamos que ha sido seleccionado/a aleatoriamente como moderador/a\n"
                + "de la Subasta #" + idSubasta
                + " en la Plataforma de Subastas Especializadas.\n\n"
                + "Fecha de vencimiento de la subasta: " + fechaVencimiento + "\n\n"
                + "Por favor ingrese a la plataforma para revisar los detalles.\n\n"
                + "Atentamente,\nPlataforma de Subastas Especializadas - UCENFOTEC";

        String registro = "[" + LocalDateTime.now() + "] "
                + "PARA: " + correoModerador + " | ASUNTO: " + asunto;
        logCorreos.add(registro);

        System.out.println("=== CORREO ENVIADO (simulacion) ===");
        System.out.println("Para   : " + correoModerador);
        System.out.println("Asunto : " + asunto);
        System.out.println(cuerpo);
        System.out.println("===================================");

        return "Notificacion enviada a: " + correoModerador;
    }

    /**
     * Simula el envio de un correo de confirmacion al ganador de una subasta.
     *
     * @param correoGanador Direccion de correo del ganador.
     * @param nombreGanador Nombre completo del ganador.
     * @param idSubasta     ID de la subasta ganada.
     * @param montoGanador  Monto de la oferta ganadora.
     * @return Mensaje confirmando el envio simulado.
     */
    public static String notificarGanador(String correoGanador,
                                           String nombreGanador,
                                           int idSubasta,
                                           double montoGanador) {
        String asunto = "Felicidades! Ha ganado la Subasta #" + idSubasta;

        String cuerpo = "Estimado/a " + nombreGanador + ",\n\n"
                + "Su oferta de $" + montoGanador
                + " ha sido la ganadora de la Subasta #" + idSubasta + ".\n\n"
                + "Ingrese a la plataforma para aceptar la adjudicacion.\n\n"
                + "Atentamente,\nPlataforma de Subastas Especializadas";

        String registro = "[" + LocalDateTime.now() + "] "
                + "PARA: " + correoGanador + " | ASUNTO: " + asunto;
        logCorreos.add(registro);

        System.out.println("=== CORREO ENVIADO (simulacion) ===");
        System.out.println("Para   : " + correoGanador);
        System.out.println("Asunto : " + asunto);
        System.out.println(cuerpo);
        System.out.println("===================================");

        return "Correo de adjudicacion enviado a: " + correoGanador;
    }

    /**
     * Retorna el historial de todos los correos enviados en la sesion.
     *
     * @return Lista con el registro de correos enviados.
     */
    public static ArrayList<String> getLogCorreos() {
        return logCorreos;
    }

    /**
     * Genera un texto con todos los correos del log para mostrar en la UI.
     *
     * @return Cadena con el historial de correos o mensaje de log vacio.
     */
    public static String mostrarLog() {
        if (logCorreos.isEmpty()) {
            return "No se han enviado correos en esta sesion.";
        }
        String resultado = "=== HISTORIAL DE CORREOS ===\n";
        for (int i = 0; i < logCorreos.size(); i++) {
            resultado += (i + 1) + ". " + logCorreos.get(i) + "\n";
        }
        return resultado;
    }
}
