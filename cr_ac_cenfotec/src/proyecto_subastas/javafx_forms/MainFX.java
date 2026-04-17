package proyecto_subastas.javafx_forms;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import proyecto_subastas.control.ControladorSubasta;
import proyecto_subastas.control.ControladorUsuario;
import proyecto_subastas.dominio.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Aplicacion JavaFX principal de la Plataforma de Subastas Especializadas.
 * Contiene todas las pantallas requeridas por la consigna del proyecto final.
 *
 * @author Steven Mendez Jimenez
 * @version 3.0
 */
public class MainFX extends Application
{

    // ── Controladores de la capa logica
    private ControladorUsuario ctrlUsuario = new ControladorUsuario();
    private ControladorSubasta ctrlSubasta = new ControladorSubasta();

    // ── Estado de la sesion activa
    private Usuario usuarioActivo = null;

    // ── Ventana principal
    private Stage ventana;

    // ── Colores de la UI
    private static final String COLOR_PRIMARIO = "#1a237e";
    private static final String COLOR_SECUNDARIO = "#5c6bc0";
    private static final String COLOR_FONDO = "#e8eaf6";
    private static final String COLOR_BLANCO = "white";
    private static final String COLOR_PELIGRO = "#c62828";
    private static final String COLOR_EXITO = "#2e7d32";

    @Override
    public void start(Stage primaryStage) {
        this.ventana = primaryStage;
        ventana.setTitle("Plataforma de Subastas Especializadas");
        ventana.setMinWidth(600);
        ventana.setMinHeight(500);
        ventana.setResizable(true);

        // Regla de negocio #1 y #2: verificar moderador al iniciar
        if (!ctrlUsuario.existeModerador()) {
            ventana.setScene(crearEscenaModerador());
        } else {
            ventana.setScene(crearEscenaLogin());
        }
        ventana.show();
    }

    // ════════════════════════════════════════════════════════════════
    // ESCENA 1: REGISTRO DE MODERADOR (solo si no existe)
    // ════════════════════════════════════════════════════════════════
    private Scene crearEscenaModerador() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + COLOR_FONDO + ";");

        Label titulo = crearTitulo("Configuracion Inicial");
        Label info = new Label("No hay moderador registrado. Registre uno para continuar.");
        info.setTextFill(Color.web(COLOR_PELIGRO));
        info.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        TextField txtNombre = crearCampo("Nombre completo");
        TextField txtId = crearCampo("Numero de identificacion");
        TextField txtCorreo = crearCampo("Correo electronico");
        PasswordField txtContrasena = new PasswordField();
        txtContrasena.setPromptText("Contrasena");
        txtContrasena.setPrefWidth(300);
        TextField txtFecha = crearCampo("Fecha nacimiento (aaaa-MM-dd, ej: 1990-05-15)");

        Label lblEstado = new Label("");

        Button btnRegistrar = crearBotonPrimario("Registrar Moderador");
        btnRegistrar.setOnAction(e -> {
            String resultado = ctrlUsuario.registrarModerador(
                    txtNombre.getText().trim(),
                    txtId.getText().trim(),
                    txtFecha.getText().trim(),
                    txtContrasena.getText().trim(),
                    txtCorreo.getText().trim()
                );
                if (resultado.startsWith("ERROR")) {
                    lblEstado.setTextFill(Color.web(COLOR_PELIGRO));
                } else {
                    lblEstado.setTextFill(Color.web(COLOR_EXITO));
                    ventana.setScene(crearEscenaLogin());
                }
                lblEstado.setText(resultado);
        });

        VBox caja = crearCaja(titulo, info,
            new Label("Nombre:"), txtNombre,
            new Label("Identificacion:"), txtId,
            new Label("Correo:"), txtCorreo,
            new Label("Contrasena:"), txtContrasena,
            new Label("Fecha nacimiento:"), txtFecha,
            btnRegistrar, lblEstado);

        root.getChildren().add(caja);
        root.setAlignment(Pos.CENTER);
        return new Scene(root, 480, 550);
    }

    // ════════════════════════════════════════════════════════════════
    // ESCENA 2: INICIO DE SESION
    // ════════════════════════════════════════════════════════════════
    private Scene crearEscenaLogin() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + COLOR_FONDO + ";");
        root.setAlignment(Pos.CENTER);

        Label titulo = crearTitulo("Plataforma de Subastas");
        Label subtitulo = new Label("Inicio de Sesion");
        subtitulo.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        subtitulo.setTextFill(Color.web(COLOR_SECUNDARIO));

        TextField txtId = crearCampo("Numero de identificacion");
        PasswordField txtContrasena = new PasswordField();
        txtContrasena.setPromptText("Contrasena");
        txtContrasena.setPrefWidth(300);

        Label lblEstado = new Label("");

        Button btnIngresar = crearBotonPrimario("Ingresar");
        Button btnRegistrarse = crearBotonSecundario("Registrarse");

        btnIngresar.setOnAction(e -> {
            Usuario u = ctrlUsuario.autenticar(txtId.getText().trim(), txtContrasena.getText().trim());
            if (u == null) {
                lblEstado.setTextFill(Color.web(COLOR_PELIGRO));
                lblEstado.setText("ERROR: Credenciales incorrectas o cuenta inactiva.");
            } else {
                usuarioActivo = u;
                ventana.setScene(crearEscenaDashboard());
            }
        });

        btnRegistrarse.setOnAction(e -> ventana.setScene(crearEscenaRegistroUsuario()));

        HBox botonesLogin = new HBox(10, btnIngresar, btnRegistrarse);
        botonesLogin.setAlignment(Pos.CENTER);

        VBox caja = crearCaja(titulo, subtitulo,
            new Label("Identificacion:"), txtId,
            new Label("Contrasena:"), txtContrasena,
            botonesLogin, lblEstado);

        root.getChildren().add(caja);
        return new Scene(root, 420, 380);
    }

    // ════════════════════════════════════════════════════════════════
    // ESCENA 3: REGISTRO DE USUARIO
    // ════════════════════════════════════════════════════════════════
    private Scene crearEscenaRegistroUsuario() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: " + COLOR_FONDO + ";");
        root.setAlignment(Pos.CENTER);

        Label titulo = crearTitulo("Registro de Usuario");

        ToggleGroup grupo = new ToggleGroup();
        RadioButton rbVendedor = new RadioButton("Vendedor");
        RadioButton rbColeccionista = new RadioButton("Coleccionista");
        rbVendedor.setToggleGroup(grupo);
        rbColeccionista.setToggleGroup(grupo);
        rbVendedor.setSelected(true);
        HBox tipoBox = new HBox(15, rbVendedor, rbColeccionista);
        tipoBox.setAlignment(Pos.CENTER);

        TextField txtNombre = crearCampo("Nombre completo");
        TextField txtId = crearCampo("Numero de identificacion");
        TextField txtCorreo = crearCampo("Correo electronico");
        PasswordField txtContrasena = new PasswordField();
        txtContrasena.setPromptText("Contrasena");
        txtContrasena.setPrefWidth(300);
        TextField txtDireccion = crearCampo("Direccion");
        TextField txtFecha = crearCampo("Fecha nacimiento (aaaa-MM-dd, ej: 1990-05-15)");

        Label lblEstado = new Label("");

        Button btnRegistrar = crearBotonPrimario("Registrar");
        Button btnVolver = crearBotonSecundario("Volver al Login");
        btnVolver.setOnAction(e -> ventana.setScene(crearEscenaLogin()));

        btnRegistrar.setOnAction(e -> {
            try {
                String fechaNacReg = txtFecha.getText().trim();
                String nombre = txtNombre.getText().trim();
                String id = txtId.getText().trim();
                String correo = txtCorreo.getText().trim();
                String contra = txtContrasena.getText().trim();
                String dir = txtDireccion.getText().trim();
                String resultado;
                if (rbVendedor.isSelected()) {
                    resultado = ctrlUsuario.registrarVendedor(nombre, id, fechaNacReg, contra, correo, dir);
                } else {
                    resultado = ctrlUsuario.registrarColeccionista(nombre, id, fechaNacReg, contra, correo, dir);
                }
                if (resultado.startsWith("ERROR")) {
                    lblEstado.setTextFill(Color.web(COLOR_PELIGRO));
                } else {
                    lblEstado.setTextFill(Color.web(COLOR_EXITO));
                }
                lblEstado.setText(resultado);
            } catch (Exception ex) {
                lblEstado.setTextFill(Color.web(COLOR_PELIGRO));
                lblEstado.setText("ERROR: " + ex.getMessage());
            }
        });

        HBox btnBox = new HBox(10, btnRegistrar, btnVolver);
        btnBox.setAlignment(Pos.CENTER);

        VBox caja = crearCaja(titulo,
            new Label("Tipo de usuario:"), tipoBox,
            new Label("Nombre:"), txtNombre,
            new Label("Identificacion:"), txtId,
            new Label("Correo:"), txtCorreo,
            new Label("Contrasena:"), txtContrasena,
            new Label("Direccion:"), txtDireccion,
            new Label("Fecha nacimiento:"), txtFecha,
            btnBox, lblEstado);

        root.getChildren().add(caja);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        return new Scene(scroll, 480, 580);
    }

    // ════════════════════════════════════════════════════════════════
    // ESCENA 4: DASHBOARD PRINCIPAL (segun rol)
    // ════════════════════════════════════════════════════════════════
    private Scene crearEscenaDashboard() {
        BorderPane raiz = new BorderPane();
        raiz.setStyle("-fx-background-color: " + COLOR_FONDO + ";");

        // -- Barra superior
        HBox barra = new HBox();
        barra.setStyle("-fx-background-color: " + COLOR_PRIMARIO + ";");
        barra.setPadding(new Insets(12, 20, 12, 20));
        barra.setAlignment(Pos.CENTER_LEFT);
        Label lblUsuario = new Label("Usuario: " + usuarioActivo.getNombreCompleto()
                + "  |  " + obtenerRol(usuarioActivo));
        lblUsuario.setTextFill(Color.WHITE);
        lblUsuario.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Button btnCerrar = new Button("Cerrar Sesion");
        btnCerrar.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: white;");
        btnCerrar.setOnAction(e -> {
            usuarioActivo = null;
            ventana.setScene(crearEscenaLogin());
        });
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        barra.getChildren().addAll(lblUsuario, spacer, btnCerrar);
        raiz.setTop(barra);

        // -- Menu lateral
        VBox menu = new VBox(5);
        menu.setPadding(new Insets(15, 10, 15, 10));
        menu.setStyle("-fx-background-color: #3949ab; -fx-min-width: 200;");

        // -- Contenido central (cambia segun opcion)
        StackPane contenido = new StackPane();
        contenido.setPadding(new Insets(20));
        Label lblBienvenida = new Label("Bienvenido/a,\n" + usuarioActivo.getNombreCompleto());
        lblBienvenida.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        lblBienvenida.setAlignment(Pos.CENTER);
        contenido.getChildren().add(lblBienvenida);

        // Opciones del menu segun rol
        agregarBotonMenu(menu, "Mis Subastas", contenido, () -> panelMisSubastas(contenido));
        agregarBotonMenu(menu, "Ver Subastas Activas", contenido, () -> panelListaSubastas(contenido));

        if (usuarioActivo instanceof Moderador) {
            agregarBotonMenu(menu, "Gestionar Usuarios", contenido, () -> panelGestionUsuarios(contenido));
            agregarBotonMenu(menu, "Gestionar Categorias", contenido, () -> panelCategorias(contenido));
            agregarBotonMenu(menu, "Asignar Moderadores", contenido, () -> panelAsignarModerador(contenido));
        }

        if (usuarioActivo instanceof Vendedor || usuarioActivo instanceof Coleccionista) {
            agregarBotonMenu(menu, "Crear Subasta", contenido, () -> panelCrearSubasta(contenido));
        }

        if (usuarioActivo instanceof Coleccionista) {
            agregarBotonMenu(menu, "Realizar Oferta", contenido, () -> panelRealizarOferta(contenido));
            agregarBotonMenu(menu, "Mis Participaciones", contenido, () -> panelMisParticipaciones(contenido));
        }

        raiz.setLeft(menu);
        raiz.setCenter(contenido);

        return new Scene(raiz, 900, 620);
    }

    // ════════════════════════════════════════════════════════════════
    // PANEL: GESTION DE USUARIOS (solo Moderador)
    // ════════════════════════════════════════════════════════════════
    private void panelGestionUsuarios(StackPane contenido) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label titulo = crearTitulo("Gestion de Usuarios");

        // Lista de usuarios
        TextArea areaUsuarios = new TextArea(ctrlUsuario.listarUsuarios());
        areaUsuarios.setEditable(false);
        areaUsuarios.setPrefHeight(200);
        areaUsuarios.setStyle("-fx-font-family: monospace;");

        // Modificar usuario
        Label lblModificar = new Label("Modificar usuario");
        lblModificar.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextField txtIdMod = crearCampo("ID del usuario a modificar");
        TextField txtNuevoNombre = crearCampo("Nuevo nombre (dejar vacio para no cambiar)");
        TextField txtNuevoCorreo = crearCampo("Nuevo correo (dejar vacio para no cambiar)");
        TextField txtNuevaDireccion = crearCampo("Nueva direccion (dejar vacio para no cambiar)");

        Label lblResultadoMod = new Label("");
        Button btnModificar = crearBotonPrimario("Modificar");
        btnModificar.setOnAction(e -> {
            String res = ctrlUsuario.modificarUsuario(
                txtIdMod.getText().trim(),
                txtNuevoNombre.getText().trim().isEmpty() ? null : txtNuevoNombre.getText().trim(),
                txtNuevoCorreo.getText().trim().isEmpty() ? null : txtNuevoCorreo.getText().trim(),
                txtNuevaDireccion.getText().trim().isEmpty() ? null : txtNuevaDireccion.getText().trim()
            );
            lblResultadoMod.setText(res);
            lblResultadoMod.setTextFill(res.startsWith("ERROR") ? Color.web(COLOR_PELIGRO) : Color.web(COLOR_EXITO));
            areaUsuarios.setText(ctrlUsuario.listarUsuarios());
        });

        // Activar / Inactivar
        Label lblEstado = new Label("Activar / Inactivar usuario");
        lblEstado.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextField txtIdEstado = crearCampo("ID del usuario");
        Label lblResultadoEst = new Label("");
        Button btnActivar = crearBotonPrimario("Activar");
        Button btnInactivar = crearBotonSecundario("Inactivar");
        btnActivar.setOnAction(e -> {
            String res = ctrlUsuario.activarUsuario(txtIdEstado.getText().trim());
            lblResultadoEst.setText(res);
            lblResultadoEst.setTextFill(res.startsWith("ERROR") ? Color.web(COLOR_PELIGRO) : Color.web(COLOR_EXITO));
            areaUsuarios.setText(ctrlUsuario.listarUsuarios());
        });
        btnInactivar.setOnAction(e -> {
            String res = ctrlUsuario.inactivarUsuario(txtIdEstado.getText().trim());
            lblResultadoEst.setText(res);
            lblResultadoEst.setTextFill(res.startsWith("ERROR") ? Color.web(COLOR_PELIGRO) : Color.web(COLOR_EXITO));
            areaUsuarios.setText(ctrlUsuario.listarUsuarios());
        });
        HBox btnEstBox = new HBox(10, btnActivar, btnInactivar);

        ScrollPane scroll = new ScrollPane();
        panel.getChildren().addAll(titulo,
            new Label("Lista de usuarios registrados:"), areaUsuarios,
            new Separator(), lblModificar,
            new Label("ID:"), txtIdMod,
            new Label("Nuevo nombre:"), txtNuevoNombre,
            new Label("Nuevo correo:"), txtNuevoCorreo,
            new Label("Nueva direccion:"), txtNuevaDireccion,
            btnModificar, lblResultadoMod,
            new Separator(), lblEstado,
            new Label("ID:"), txtIdEstado,
            btnEstBox, lblResultadoEst);

        scroll.setContent(panel);
        scroll.setFitToWidth(true);
        contenido.getChildren().setAll(scroll);
    }

    // ════════════════════════════════════════════════════════════════
    // PANEL: CATEGORIAS (solo Moderador)
    // ════════════════════════════════════════════════════════════════
    private void panelCategorias(StackPane contenido) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label titulo = crearTitulo("Gestion de Categorias");

        TextArea areaCats = new TextArea(ctrlSubasta.listarCategorias());
        areaCats.setEditable(false);
        areaCats.setPrefHeight(150);
        areaCats.setStyle("-fx-font-family: monospace;");

        TextField txtNombreCat = crearCampo("Nombre de la categoria");
        TextField txtDescCat = crearCampo("Descripcion");
        Label lblResCat = new Label("");

        Button btnCrearCat = crearBotonPrimario("Crear Categoria");
        btnCrearCat.setOnAction(e -> {
            String res = ctrlSubasta.crearCategoria(
                txtNombreCat.getText().trim(),
                txtDescCat.getText().trim()
            );
            lblResCat.setText(res);
            lblResCat.setTextFill(res.startsWith("ERROR") ? Color.web(COLOR_PELIGRO) : Color.web(COLOR_EXITO));
            areaCats.setText(ctrlSubasta.listarCategorias());
            txtNombreCat.clear();
            txtDescCat.clear();
        });

        panel.getChildren().addAll(titulo,
            new Label("Categorias existentes:"), areaCats, new Separator(),
            new Label("Nombre:"), txtNombreCat,
            new Label("Descripcion:"), txtDescCat,
            btnCrearCat, lblResCat);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        contenido.getChildren().setAll(scroll);
    }

    // ════════════════════════════════════════════════════════════════
    // PANEL: ASIGNAR MODERADOR A COLECCIONISTA (solo Moderador)
    // ════════════════════════════════════════════════════════════════
    private void panelAsignarModerador(StackPane contenido) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label titulo = crearTitulo("Asignar Rol de Moderador");

        Label info = new Label("Seleccione un coleccionista para asignarle el rol de moderador de subastas.");
        info.setWrapText(true);

        TextArea areaColeccionistas = new TextArea();
        areaColeccionistas.setEditable(false);
        areaColeccionistas.setPrefHeight(150);
        areaColeccionistas.setStyle("-fx-font-family: monospace;");

        // Mostrar coleccionistas
        StringBuilder sbCol = new StringBuilder();
        ArrayList<Coleccionista> cols = ctrlUsuario.listarColeccionistas();
        for (Coleccionista c : cols) {
            sbCol.append(c.toString()).append("\n");
        }
        areaColeccionistas.setText(sbCol.length() == 0 ? "No hay coleccionistas registrados." : sbCol.toString());

        TextField txtIdCol = crearCampo("ID del coleccionista a designar");
        Label lblRes = new Label("");

        Button btnAsignar = crearBotonPrimario("Designar como Moderador");
        btnAsignar.setOnAction(e -> {
            String res = ctrlUsuario.asignarComoModerador(txtIdCol.getText().trim());
            lblRes.setText(res);
            lblRes.setTextFill(res.startsWith("ERROR") ? Color.web(COLOR_PELIGRO) : Color.web(COLOR_EXITO));
        });

        panel.getChildren().addAll(titulo, info,
            new Label("Coleccionistas:"), areaColeccionistas,
            new Separator(),
            new Label("ID del coleccionista:"), txtIdCol,
            btnAsignar, lblRes);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        contenido.getChildren().setAll(scroll);
    }

    // ════════════════════════════════════════════════════════════════
    // PANEL: CREAR SUBASTA (Vendedor o Coleccionista)
    // ════════════════════════════════════════════════════════════════
    private void panelCrearSubasta(StackPane contenido) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label titulo = crearTitulo("Crear Nueva Subasta");

        TextField txtFecha = crearCampo("Fecha de vencimiento (dd/mm/aaaa)");
        TextField txtPrecio = crearCampo("Precio minimo ($)");

        Label lblObjetos = new Label("Objetos a subastar:");
        lblObjetos.setFont(Font.font("Arial", FontWeight.BOLD, 13));

        ArrayList<ObjetoSubasta> objetosTemporal = new ArrayList<>();
        TextArea areaObjetos = new TextArea("No hay objetos agregados aun.");
        areaObjetos.setEditable(false);
        areaObjetos.setPrefHeight(100);

        TextField txtNombreObj = crearCampo("Nombre del objeto");
        TextField txtDescObj = crearCampo("Descripcion");
        TextField txtFechaObj = crearCampo("Fecha de compra (aaaa-MM-dd, ej: 2010-03-20)");
        ComboBox<String> cmbEstado = new ComboBox<>();
        cmbEstado.getItems().addAll("Nuevo", "Usado", "Antiguo sin abrir");
        cmbEstado.setPromptText("Seleccione estado");
        cmbEstado.setPrefWidth(300);

        Label lblResObj = new Label("");
        Button btnAgregarObj = crearBotonSecundario("Agregar Objeto");
        btnAgregarObj.setOnAction(e -> {
            try {
                if (cmbEstado.getValue() == null) {
                    lblResObj.setText("Seleccione un estado.");
                    lblResObj.setTextFill(Color.web(COLOR_PELIGRO));
                    return;
                }
                ObjetoSubasta obj = ctrlSubasta.crearObjeto(
                    txtNombreObj.getText().trim(),
                    txtDescObj.getText().trim(),
                    cmbEstado.getValue(),
                    txtFechaObj.getText().trim()
                );
                objetosTemporal.add(obj);
                StringBuilder sb = new StringBuilder();
                for (ObjetoSubasta o : objetosTemporal) {
                    sb.append("- ").append(o.toString()).append("\n");
                }
                areaObjetos.setText(sb.toString());
                txtNombreObj.clear();
                txtDescObj.clear();
                txtFechaObj.clear();
                cmbEstado.setValue(null);
                lblResObj.setText("Objeto agregado.");
                lblResObj.setTextFill(Color.web(COLOR_EXITO));
            } catch (Exception ex) {
                lblResObj.setText("ERROR: " + ex.getMessage());
                lblResObj.setTextFill(Color.web(COLOR_PELIGRO));
            }
        });

        Label lblResSubasta = new Label("");
        Button btnCrearSubasta = crearBotonPrimario("Crear Subasta");
        btnCrearSubasta.setOnAction(e -> {
            try {
                double precioMin = Double.parseDouble(txtPrecio.getText().trim());
                String res = ctrlSubasta.crearSubasta(
                    usuarioActivo, objetosTemporal, precioMin, txtFecha.getText().trim()
                );
                if (!res.startsWith("ERROR")) {
                    // Asignar moderador aleatorio
                    ArrayList<Coleccionista> mods = ctrlUsuario.listarColeccionistasModerador();
                    if (!mods.isEmpty()) {
                        Subasta ultimaSubasta = ctrlSubasta.getSubastas().get(
                            ctrlSubasta.getSubastas().size() - 1
                        );
                        String resMod = ctrlSubasta.asignarModeradorAleatorio(ultimaSubasta, mods);
                        res += "\n" + resMod;
                    }
                    objetosTemporal.clear();
                    areaObjetos.setText("No hay objetos agregados aun.");
                    txtFecha.clear();
                    txtPrecio.clear();
                    lblResSubasta.setTextFill(Color.web(COLOR_EXITO));
                } else {
                    lblResSubasta.setTextFill(Color.web(COLOR_PELIGRO));
                }
                lblResSubasta.setText(res);
            } catch (NumberFormatException ex) {
                lblResSubasta.setText("ERROR: El precio debe ser un numero.");
                lblResSubasta.setTextFill(Color.web(COLOR_PELIGRO));
            }
        });

        panel.getChildren().addAll(titulo,
            new Label("Fecha vencimiento:"), txtFecha,
            new Label("Precio minimo:"), txtPrecio,
            new Separator(), lblObjetos,
            new Label("Nombre obj:"), txtNombreObj,
            new Label("Descripcion:"), txtDescObj,
            new Label("Fecha compra:"), txtFechaObj,
            new Label("Estado:"), cmbEstado,
            btnAgregarObj, lblResObj,
            new Label("Objetos en la subasta:"), areaObjetos,
            new Separator(),
            btnCrearSubasta, lblResSubasta);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        contenido.getChildren().setAll(scroll);
    }

    // ════════════════════════════════════════════════════════════════
    // PANEL: LISTA DE SUBASTAS ACTIVAS con contador de tiempo
    // ════════════════════════════════════════════════════════════════
    private void panelListaSubastas(StackPane contenido) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label titulo = crearTitulo("Subastas Activas");

        TextArea areaSubastas = new TextArea(ctrlSubasta.listarSubastas());
        areaSubastas.setEditable(false);
        areaSubastas.setPrefHeight(300);
        areaSubastas.setStyle("-fx-font-family: monospace;");

        Button btnRefrescar = crearBotonSecundario("Actualizar lista");
        btnRefrescar.setOnAction(e -> areaSubastas.setText(ctrlSubasta.listarSubastas()));

        // Gestion de subasta (cerrar, adjudicar, aceptar)
        Label lblGestion = new Label("Gestionar Subasta");
        lblGestion.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextField txtIdSubasta = crearCampo("ID de la subasta");
        Label lblResGestion = new Label("");

        Button btnCerrar = crearBotonSecundario("Cerrar Subasta");
        Button btnAdjudicar = crearBotonPrimario("Adjudicar Ganador");
        Button btnAceptar = crearBotonPrimario("Aceptar Adjudicacion");
        Button btnConfEntrega = crearBotonSecundario("Confirmar Entrega");

        btnCerrar.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtIdSubasta.getText().trim());
                String res = ctrlSubasta.cerrarSubasta(id);
                mostrarResultado(lblResGestion, res);
                areaSubastas.setText(ctrlSubasta.listarSubastas());
            } catch (NumberFormatException ex) {
                mostrarResultado(lblResGestion, "ERROR: ID invalido.");
            }
        });

        btnAdjudicar.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtIdSubasta.getText().trim());
                String res = ctrlSubasta.adjudicarGanador(id);
                mostrarResultado(lblResGestion, res);
                areaSubastas.setText(ctrlSubasta.listarSubastas());
            } catch (NumberFormatException ex) {
                mostrarResultado(lblResGestion, "ERROR: ID invalido.");
            }
        });

        btnAceptar.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtIdSubasta.getText().trim());
                String res = ctrlSubasta.aceptarAdjudicacion(id, usuarioActivo.getNombreCompleto());
                mostrarResultado(lblResGestion, res);
                areaSubastas.setText(ctrlSubasta.listarSubastas());
            } catch (NumberFormatException ex) {
                mostrarResultado(lblResGestion, "ERROR: ID invalido.");
            }
        });

        btnConfEntrega.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtIdSubasta.getText().trim());
                String res = ctrlSubasta.confirmarEntrega(id);
                mostrarResultado(lblResGestion, res);
            } catch (NumberFormatException ex) {
                mostrarResultado(lblResGestion, "ERROR: ID invalido.");
            }
        });

        HBox btnGestionBox = new HBox(8, btnCerrar, btnAdjudicar, btnAceptar, btnConfEntrega);
        btnGestionBox.setAlignment(Pos.CENTER_LEFT);

        panel.getChildren().addAll(titulo, btnRefrescar, areaSubastas,
            new Separator(), lblGestion,
            new Label("ID de subasta:"), txtIdSubasta,
            btnGestionBox, lblResGestion);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        contenido.getChildren().setAll(scroll);
    }

    // ════════════════════════════════════════════════════════════════
    // PANEL: REALIZAR OFERTA (solo Coleccionista)
    // ════════════════════════════════════════════════════════════════
    private void panelRealizarOferta(StackPane contenido) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label titulo = crearTitulo("Realizar Oferta");

        TextArea areaSubastas = new TextArea(ctrlSubasta.listarSubastas());
        areaSubastas.setEditable(false);
        areaSubastas.setPrefHeight(200);
        areaSubastas.setStyle("-fx-font-family: monospace;");

        TextField txtIdSub = crearCampo("ID de la subasta");
        TextField txtMonto = crearCampo("Monto a ofertar ($)");
        Label lblRes = new Label("");

        Button btnOfertar = crearBotonPrimario("Confirmar Oferta");
        btnOfertar.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtIdSub.getText().trim());
                double monto = Double.parseDouble(txtMonto.getText().trim());
                String res = ctrlSubasta.registrarOferta(id, usuarioActivo, monto);
                mostrarResultado(lblRes, res);
                if (!res.startsWith("ERROR")) {
                    areaSubastas.setText(ctrlSubasta.listarSubastas());
                    txtIdSub.clear();
                    txtMonto.clear();
                }
            } catch (NumberFormatException ex) {
                mostrarResultado(lblRes, "ERROR: Valores numericos invalidos.");
            }
        });

        Button btnRefrescar = crearBotonSecundario("Actualizar subastas");
        btnRefrescar.setOnAction(e -> areaSubastas.setText(ctrlSubasta.listarSubastas()));

        panel.getChildren().addAll(titulo,
            new Label("Subastas disponibles:"), areaSubastas, btnRefrescar,
            new Separator(),
            new Label("ID de subasta:"), txtIdSub,
            new Label("Monto ($):"), txtMonto,
            btnOfertar, lblRes);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        contenido.getChildren().setAll(scroll);
    }

    // ════════════════════════════════════════════════════════════════
    // PANEL: MIS SUBASTAS con contador visual de tiempo
    // ════════════════════════════════════════════════════════════════
    private void panelMisSubastas(StackPane contenido) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label titulo = crearTitulo("Mis Subastas");

        TextArea areaSubastas = new TextArea(
            ctrlSubasta.listarMisSubastas(usuarioActivo.getNombreCompleto())
        );
        areaSubastas.setEditable(false);
        areaSubastas.setPrefHeight(250);
        areaSubastas.setStyle("-fx-font-family: monospace;");

        // Contador de tiempo restante
        Label lblContador = new Label("Contador de tiempo - Ingrese ID de subasta:");
        lblContador.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextField txtIdCont = crearCampo("ID de la subasta");
        Label lblTiempo = new Label("-- d  -- h  -- m  -- s");
        lblTiempo.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        lblTiempo.setTextFill(Color.web(COLOR_PRIMARIO));

        // Timer para actualizar el contador cada segundo
        Timer[] timer = {null};

        Button btnIniciarContador = crearBotonPrimario("Iniciar Contador");
        btnIniciarContador.setOnAction(e -> {
            try {
                int id = Integer.parseInt(txtIdCont.getText().trim());
                Subasta s = ctrlSubasta.buscarPorId(id);
                if (s == null) {
                    lblTiempo.setText("Subasta no encontrada.");
                    return;
                }
                if (timer[0] != null) timer[0].cancel();
                timer[0] = new Timer();
                timer[0].scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        String countdown = calcularTiempoRestante(s.getFechaVencimiento());
                        Platform.runLater(() -> lblTiempo.setText(countdown));
                    }
                }, 0, 1000);
            } catch (NumberFormatException ex) {
                lblTiempo.setText("ID invalido.");
            }
        });

        Button btnRefrescar = crearBotonSecundario("Refrescar lista");
        btnRefrescar.setOnAction(e -> areaSubastas.setText(
            ctrlSubasta.listarMisSubastas(usuarioActivo.getNombreCompleto())
        ));

        panel.getChildren().addAll(titulo, btnRefrescar, areaSubastas,
            new Separator(), lblContador,
            new Label("ID de subasta:"), txtIdCont,
            btnIniciarContador, lblTiempo);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        contenido.getChildren().setAll(scroll);
    }

    // ════════════════════════════════════════════════════════════════
    // PANEL: MIS PARTICIPACIONES (coleccionista como oferente)
    // ════════════════════════════════════════════════════════════════
    private void panelMisParticipaciones(StackPane contenido) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(15));

        Label titulo = crearTitulo("Mis Participaciones como Oferente");

        TextArea areaPart = new TextArea(
            ctrlSubasta.listarSubastasComoOferente(usuarioActivo.getNombreCompleto())
        );
        areaPart.setEditable(false);
        areaPart.setPrefHeight(300);
        areaPart.setStyle("-fx-font-family: monospace;");

        Button btnRefrescar = crearBotonSecundario("Actualizar");
        btnRefrescar.setOnAction(e -> areaPart.setText(
            ctrlSubasta.listarSubastasComoOferente(usuarioActivo.getNombreCompleto())
        ));

        // Calificacion
        Label lblCalif = new Label("Calificar a la contraparte");
        lblCalif.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        TextField txtIdCalif = crearCampo("ID de la subasta");
        ComboBox<Integer> cmbCalif = new ComboBox<>();
        cmbCalif.getItems().addAll(1, 2, 3, 4, 5);
        cmbCalif.setPromptText("Calificacion (1-5)");
        Label lblResCalif = new Label("");

        Button btnCalificar = crearBotonPrimario("Calificar");
        btnCalificar.setOnAction(e -> {
            if (cmbCalif.getValue() == null) {
                lblResCalif.setText("Seleccione una calificacion.");
                return;
            }
            try {
                int id = Integer.parseInt(txtIdCalif.getText().trim());
                String res = ctrlSubasta.calificar(id, cmbCalif.getValue(), true);
                mostrarResultado(lblResCalif, res);
            } catch (NumberFormatException ex) {
                mostrarResultado(lblResCalif, "ERROR: ID invalido.");
            }
        });

        panel.getChildren().addAll(titulo, btnRefrescar, areaPart,
            new Separator(), lblCalif,
            new Label("ID subasta:"), txtIdCalif,
            new Label("Calificacion:"), cmbCalif,
            btnCalificar, lblResCalif);

        ScrollPane scroll = new ScrollPane(panel);
        scroll.setFitToWidth(true);
        contenido.getChildren().setAll(scroll);
    }

    // ════════════════════════════════════════════════════════════════
    // METODOS AUXILIARES
    // ════════════════════════════════════════════════════════════════

    /**
     * Calcula el tiempo restante hasta una fecha de vencimiento en formato "dd/MM/yyyy".
     * Retorna un string formateado con dias, horas, minutos y segundos.
     *
     * @param fechaVencimiento Fecha en formato "dd/MM/yyyy".
     * @return Cadena con el tiempo restante o "VENCIDA" si ya paso.
     */
    private String calcularTiempoRestante(String fechaVencimiento) {
        try {
            String[] partes = fechaVencimiento.split("/");
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int anio = Integer.parseInt(partes[2]);

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(anio, mes - 1, dia, 23, 59, 59);
            long diferencia = cal.getTimeInMillis() - System.currentTimeMillis();

            if (diferencia <= 0) return "SUBASTA VENCIDA";

            long dias = diferencia / (1000 * 60 * 60 * 24);
            long horas = (diferencia % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutos = (diferencia % (1000 * 60 * 60)) / (1000 * 60);
            long segundos = (diferencia % (1000 * 60)) / 1000;

            return dias + "d  " + horas + "h  " + minutos + "m  " + segundos + "s";
        } catch (Exception e) {
            return "Formato de fecha invalido";
        }
    }

    /** Obtiene el rol del usuario en texto legible. */
    private String obtenerRol(Usuario u) {
        if (u instanceof Moderador) return "Moderador";
        if (u instanceof Vendedor) return "Vendedor";
        if (u instanceof Coleccionista) {
            Coleccionista c = (Coleccionista) u;
            return c.isEsModerador() ? "Coleccionista (Moderador de Subastas)" : "Coleccionista";
        }
        return "Usuario";
    }

    /** Agrega un boton de menu lateral que cambia el panel central. */
    private void agregarBotonMenu(VBox menu, String texto, StackPane contenido, Runnable accion) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; "
                + "-fx-font-size: 13; -fx-cursor: hand; -fx-alignment: center-left; "
                + "-fx-padding: 8 10 8 10;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #283593; -fx-text-fill: white; "
                + "-fx-font-size: 13; -fx-cursor: hand; -fx-alignment: center-left; "
                + "-fx-padding: 8 10 8 10;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-text-fill: white; "
                + "-fx-font-size: 13; -fx-cursor: hand; -fx-alignment: center-left; "
                + "-fx-padding: 8 10 8 10;"));
        btn.setOnAction(e -> accion.run());
        menu.getChildren().add(btn);
    }

    /** Muestra el resultado de una operacion en un label. */
    private void mostrarResultado(Label lbl, String mensaje) {
        lbl.setText(mensaje);
        lbl.setTextFill(mensaje.startsWith("ERROR") ? Color.web(COLOR_PELIGRO) : Color.web(COLOR_EXITO));
    }

    /** Crea un TextField con prompt text estandar. */
    private TextField crearCampo(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(300);
        return tf;
    }

    /** Crea un boton con estilo primario. */
    private Button crearBotonPrimario(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: " + COLOR_PRIMARIO + "; -fx-text-fill: white; "
                + "-fx-font-weight: bold; -fx-cursor: hand;");
        return btn;
    }

    /** Crea un boton con estilo secundario. */
    private Button crearBotonSecundario(String texto) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: transparent; -fx-border-color: " + COLOR_PRIMARIO + "; "
                + "-fx-text-fill: " + COLOR_PRIMARIO + "; -fx-cursor: hand;");
        return btn;
    }

    /** Crea un label de titulo estandar. */
    private Label crearTitulo(String texto) {
        Label lbl = new Label(texto);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        lbl.setTextFill(Color.web(COLOR_PRIMARIO));
        return lbl;
    }

    /** Crea una VBox con fondo blanco y sombra como caja de contenido. */
    private VBox crearCaja(javafx.scene.Node... nodos) {
        VBox caja = new VBox(10);
        caja.setPadding(new Insets(25));
        caja.setStyle("-fx-background-color: white; -fx-background-radius: 8; "
                + "-fx-effect: dropshadow(gaussian, #aaa, 10, 0, 0, 2);");
        caja.setMaxWidth(400);
        caja.getChildren().addAll(nodos);
        return caja;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

