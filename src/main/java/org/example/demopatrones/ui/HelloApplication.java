package org.example.demopatrones.ui;

import org.example.demopatrones.decorator.EncabezadoDecorador;
import org.example.demopatrones.decorator.MarcoDecorador;
import org.example.demopatrones.decorator.WatermarkDecorador;
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
        Button btnBase = new Button("1. Servicio Base");
        Button btnProxy = new Button("2. Proxy (Caché)");
        Button btnDecorator = new Button("3. Decorator (Capas)");
        Button btnSinergia = new Button("4. Proxy + Decorator (Sinergia)");

        // Estilos para los botones
        btnBase.setStyle("-fx-base: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");
        btnProxy.setStyle("-fx-base: #5cb85c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnDecorator.setStyle("-fx-base: #0275d8; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSinergia.setStyle("-fx-base: #6f42c1; -fx-text-fill: white; -fx-font-weight: bold;");

        Label lblTiempo = new Label("0 ms");
        Label lblEstado = new Label("En espera");

        StackPane areaReporte = new StackPane();
        areaReporte.setPrefSize(520, 290);
        areaReporte.setStyle("-fx-border-color: #ccc; -fx-background-color: #ffffff;");

        TextArea txtAuditoria = new TextArea();
        txtAuditoria.setEditable(false);
        txtAuditoria.setPrefHeight(150);
        txtAuditoria.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #00ff00; -fx-font-family: monospace;");

        // --- 1. EVENTO BASE ---
        btnBase.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Cargando servicio base...");

            txtAuditoria.appendText("=== PRUEBA 1: SERVICIO BASE (SIN PATRONES) ===\n");
            txtAuditoria.appendText("[CLIENTE] Pidiendo el gráfico directamente a la Base de Datos...\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();
                Node reporte = baseService.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "Procesado en BD Real");
                    txtAuditoria.appendText("[BASE BD] Consulta pesada terminada en " + (t2 - t1) + " ms.\n");
                    txtAuditoria.appendText("[PROBLEMA] Si volvemos a pedirlo, va a volver a tardar lo mismo porque no guarda nada.\n");
                });
            }).start();
        });

        // --- 2. EVENTO PROXY ---
        btnProxy.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Consultando Proxy...");

            txtAuditoria.appendText("=== PRUEBA 2: PATRÓN PROXY (MEMORIA Y CACHÉ) ===\n");
            txtAuditoria.appendText("[CLIENTE] Pidiendo el gráfico al Proxy intermediario...\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();
                txtAuditoria.appendText("[PROXY] Revisando si ya tenemos guardado el gráfico de '" + txtUsuario.getText() + "'...\n");

                Node reporte = proxyService.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    if (proxyService.isVieneDeCache()) {
                        mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "ENCONTRADO EN MEMORIA (CACHÉ)");
                        txtAuditoria.appendText("[PROXY] ¡RESULTADO: Ya estaba guardado!\n");
                        txtAuditoria.appendText("[PROXY] No fuimos a la Base de Datos. Se entregó de inmediato en " + (t2 - t1) + " ms.\n");
                    } else {
                        mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "NO ESTABA EN MEMORIA (GUARDANDO)");
                        txtAuditoria.appendText("[PROXY] RESULTADO: No estaba guardado.\n");
                        txtAuditoria.appendText("[PROXY -> BD] Fuimos a la Base de Datos a crearlo (" + (t2 - t1) + " ms).\n");
                        txtAuditoria.appendText("[PROXY] Ya lo guardamos en memoria para la próxima vez.\n");
                    }
                });
            }).start();
        });

        // --- 3. EVENTO DECORATOR ---
        btnDecorator.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Aplicando Decoradores...");

            txtAuditoria.appendText("=== PRUEBA 3: PATRÓN DECORATOR (AGREGAR DETALLES VISUALES) ===\n");
            txtAuditoria.appendText("[CLIENTE] Armando el diseño por partes:\n");
            txtAuditoria.appendText("          -> Marca de agua\n");
            txtAuditoria.appendText("             -> Texto de Encabezado\n");
            txtAuditoria.appendText("                -> Marco azul\n");
            txtAuditoria.appendText("                   -> Gráfico original\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();

                ReporteService decorado = new WatermarkDecorador(
                        new EncabezadoDecorador(
                                new MarcoDecorador(baseService)
                        )
                );

                Node reporteFinal = decorado.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    mostrarReporte(areaReporte, reporteFinal, lblTiempo, (t2 - t1), lblEstado, "Detalles Visuales Agregados");
                    txtAuditoria.appendText("[PASO 1] El servicio genera el gráfico normal.\n");
                    txtAuditoria.appendText("[PASO 2] 'MarcoDecorador' le pone el borde azul alrededor.\n");
                    txtAuditoria.appendText("[PASO 3] 'EncabezadoDecorador' le agrega el título rojo arriba.\n");
                    txtAuditoria.appendText("[PASO 4] 'WatermarkDecorador' le pone la marca de agua transparente encima.\n");
                    txtAuditoria.appendText("[VENTAJA] Le agregamos de todo al gráfico sin modificar su código original.\n");
                });
            }).start();
        });

        // --- 4. EVENTO SINERGIA TOTAL (PROXY + DECORATOR) ---
        btnSinergia.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Uniendo Proxy + Decorator...");

            txtAuditoria.appendText("=== PRUEBA 4: COMBINACIÓN (PROXY + DECORATOR) ===\n");
            txtAuditoria.appendText("[ORDEN DE TRABAJO] ¿Cómo colaboran?\n");
            txtAuditoria.appendText("   [Cliente] -> Pide diseño -> Decoradores -> Le piden el gráfico al Proxy -> Proxy busca en Memoria/BD\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();

                // Los Decoradores envuelven al Proxy
                ReporteService servicioCombinado = new EncabezadoDecorador(
                        new MarcoDecorador(proxyService)
                );

                txtAuditoria.appendText("[EJECUCIÓN] Iniciando la petición...\n");
                Node reporteCombinado = servicioCombinado.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    boolean cacheHit = proxyService.isVieneDeCache();
                    String estadoMsg = cacheHit ? "RESPUESTA RÁPIDA CON DISEÑO COMPLETO" : "PRIMERA CARGA CON DISEÑO";

                    mostrarReporte(areaReporte, reporteCombinado, lblTiempo, (t2 - t1), lblEstado, estadoMsg);

                    txtAuditoria.appendText("\n--- PARTE 1: TRABAJO DEL PROXY (VELOCIDAD) ---\n");
                    if (cacheHit) {
                        txtAuditoria.appendText("[PROXY] ¡Encontrado en memoria!\n");
                        txtAuditoria.appendText("[PROXY] Nos entregó el gráfico rápido en " + (t2 - t1) + " ms sin ir a la BD.\n");
                    } else {
                        txtAuditoria.appendText("[PROXY] No estaba en memoria.\n");
                        txtAuditoria.appendText("[PROXY] Fue a la BD por primera vez y guardó el gráfico para después.\n");
                    }

                    txtAuditoria.appendText("\n--- PARTE 2: TRABAJO DEL DECORATOR (DISEÑO) ---\n");
                    txtAuditoria.appendText("[DECORATOR] 1. Tomó el gráfico que dio el Proxy y le puso el marco azul.\n");
                    txtAuditoria.appendText("[DECORATOR] 2. Luego le puso el texto rojo arriba.\n");

                    txtAuditoria.appendText("\n--- RESUMEN SENCILLO ---\n");
                    txtAuditoria.appendText("[ÉXITO] El Proxy hace que abra súper rápido (" + (t2 - t1) + " ms) y el Decorator le pone los adornos visuales.");
                });
            }).start();
        });

        // Layout Principal
        HBox topBox = new HBox(8, new Label("Usuario:"), txtUsuario, btnBase, btnProxy, btnDecorator, btnSinergia);
        topBox.setAlignment(Pos.CENTER_LEFT);

        HBox metrics = new HBox(20, new Label("Tiempo respuesta:"), lblTiempo, new Label("Estado del sistema:"), lblEstado);

        VBox layout = new VBox(10, topBox, metrics, areaReporte, new Label("Auditor de Ejecución (Paso a Paso):"), txtAuditoria);
        layout.setPadding(new Insets(15));

        primaryStage.setScene(new Scene(layout, 820, 600));
        primaryStage.show();
    }

    private void prepararEjecucion(StackPane panel, Label estado, TextArea auditoria, String msg) {
        panel.getChildren().clear();
        panel.getChildren().add(new ProgressIndicator());
        estado.setText(msg);
        auditoria.clear();
    }

    private void mostrarReporte(StackPane panel, Node reporte, Label lblTiempo, long tiempo, Label lblEstado, String estadoMsg) {
        panel.getChildren().clear();
        panel.getChildren().add(reporte);
        lblTiempo.setText(tiempo + " ms");
        lblEstado.setText(estadoMsg);
    }
}