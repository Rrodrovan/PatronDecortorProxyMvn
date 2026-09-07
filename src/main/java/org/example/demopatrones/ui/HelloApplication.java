package org.example.demopatrones.ui;

import org.example.demopatrones.decorator.EncabezadoDecorador;
import org.example.demopatrones.decorator.MarcoDecorador;
import org.example.demopatrones.domain.ReporteService;
import org.example.demopatrones.proxy.ReporteProxy;
import org.example.demopatrones.service.ReporteServiceImpl;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private final ReporteServiceImpl baseService = new ReporteServiceImpl();
    private final ReporteProxy proxyService = new ReporteProxy(baseService);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Demostración de Patrones: Proxy & Decorator");

        TextField txtUsuario = new TextField("Carlos");
        Button btnBase = new Button("1. Cargar Base (Sin Caché)");
        Button btnProxy = new Button("2. Cargar con Proxy (Caché)");
        Button btnDecorator = new Button("3. Cargar con Decoradores (Visuales)");

        Label lblTiempo = new Label("0 ms");
        Label lblEstado = new Label("En espera");

        StackPane areaReporte = new StackPane();
        areaReporte.setPrefSize(500, 280);
        areaReporte.setStyle("-fx-border-color: #ccc; -fx-background-color: #ffffff;");

        // Renombrado a Área de Auditoría
        TextArea txtAuditoria = new TextArea();
        txtAuditoria.setEditable(false);
        txtAuditoria.setPrefHeight(140);
        txtAuditoria.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ff00; -fx-font-family: monospace;");

        // --- EVENTO 1: SERVICIO BASE ---
        btnBase.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Cargando reporte base...");

            txtAuditoria.appendText("=== INICIANDO PAGO/SOLICITUD DIRECTA ===\n");
            txtAuditoria.appendText("[CLIENTE] Solicitando gráfico directamente a 'ReporteServiceImpl'...\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();
                Node reporte = baseService.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "Procesado en BD Real");
                    txtAuditoria.appendText("[BASE] BD consultada con éxito. Tiempo de proceso: " + (t2 - t1) + "ms.\n");
                    txtAuditoria.appendText("[CLIENTE] Reporte recibido sin capas ni intercepciones.\n");
                });
            }).start();
        });

        // --- EVENTO 2: PROXY ---
        btnProxy.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Consultando Proxy...");

            txtAuditoria.appendText("=== INICIANDO INTERCEPCIÓN VÍA PROXY ===\n");
            txtAuditoria.appendText("[CLIENTE] Solicitando gráfico a 'ReporteProxy'...\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();

                txtAuditoria.appendText("[PROXY] Verificando en la memoria caché para el usuario '" + txtUsuario.getText() + "'...\n");
                Node reporte = proxyService.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    if (proxyService.isVieneDeCache()) {
                        mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "HIT CACHÉ (Memoria)");
                        txtAuditoria.appendText("[PROXY] ¡ÉXITO (HIT)! El reporte ya existía en caché.\n");
                        txtAuditoria.appendText("[PROXY] Se omitió la consulta a la BD Real. Tiempo total: " + (t2 - t1) + "ms.\n");
                    } else {
                        mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "MISS CACHÉ (Guardado)");
                        txtAuditoria.appendText("[PROXY] FALLO (MISS). El reporte no estaba en caché.\n");
                        txtAuditoria.appendText("[PROXY -> BASE] Delegando petición al servicio real en BD...\n");
                        txtAuditoria.appendText("[PROXY] Objeto gráfico guardado en memoria para futuras peticiones.\n");
                    }
                });
            }).start();
        });

        // --- EVENTO 3: DECORATOR ---
        btnDecorator.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Aplicando Decoradores...");

            txtAuditoria.appendText("=== INICIANDO ENCADENAMIENTO DE DECORADORES ===\n");
            txtAuditoria.appendText("[CLIENTE] Ensamblando envoltorios (Wrappers):\n");
            txtAuditoria.appendText("          -> EncabezadoDecorador\n");
            txtAuditoria.appendText("             -> MarcoDecorador\n");
            txtAuditoria.appendText("                -> ReporteServiceImpl (Servicio Real)\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();

                // Construcción de la cebolla (patrón Decorator)
                ReporteService decorado = new EncabezadoDecorador(new MarcoDecorador(baseService));

                txtAuditoria.appendText("[EJECUCIÓN] Llamando a 'generarReporte()' en la capa exterior...\n");
                Node reporteFinal = decorado.generarReporte(txtUsuario.getText());

                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    mostrarReporte(areaReporte, reporteFinal, lblTiempo, (t2 - t1), lblEstado, "Reporte Decorado");
                    txtAuditoria.appendText("[BASE] Servicio real generó el gráfico básico.\n");
                    txtAuditoria.appendText("[DECORATOR] 'MarcoDecorador' recibió el gráfico y le agregó borde y sombra.\n");
                    txtAuditoria.appendText("[DECORATOR] 'EncabezadoDecorador' recibió el resultado anterior y le añadió el membrete rojo.\n");
                    txtAuditoria.appendText("[CLIENTE] Objeto final totalmente transformado recibido en " + (t2 - t1) + "ms.\n");
                });
            }).start();
        });

        // Layout
        HBox topBox = new HBox(10, new Label("Usuario:"), txtUsuario, btnBase, btnProxy, btnDecorator);
        topBox.setAlignment(Pos.CENTER_LEFT);

        HBox metrics = new HBox(20, new Label("Tiempo:"), lblTiempo, new Label("Estado:"), lblEstado);

        VBox layout = new VBox(10, topBox, metrics, areaReporte, new Label("Auditor de Ejecución:"), txtAuditoria);
        layout.setPadding(new Insets(15));

        primaryStage.setScene(new Scene(layout, 720, 580));
        primaryStage.show();
    }

    private void prepararEjecucion(StackPane panel, Label estado, TextArea auditoria, String msg) {
        panel.getChildren().clear();
        panel.getChildren().add(new ProgressIndicator());
        estado.setText(msg);
        auditoria.clear(); // Limpia la consola de auditoría en cada llamada
    }

    private void mostrarReporte(StackPane panel, Node reporte, Label lblTiempo, long tiempo, Label lblEstado, String estadoMsg) {
        panel.getChildren().clear();
        panel.getChildren().add(reporte);
        lblTiempo.setText(tiempo + " ms");
        lblEstado.setText(estadoMsg);
    }
}