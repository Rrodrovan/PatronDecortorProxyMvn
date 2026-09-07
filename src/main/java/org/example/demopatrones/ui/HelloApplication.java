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
        primaryStage.setTitle("Demostración Arquitectura de Patrones: Proxy & Decorator");

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
            txtAuditoria.appendText("[CLIENTE] Solicitando informe directamente a 'ReporteServiceImpl'...\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();
                Node reporte = baseService.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "Procesado en BD Real");
                    txtAuditoria.appendText("[BASE BD] Simulación de consulta pesada completada (" + (t2 - t1) + "ms).\n");
                    txtAuditoria.appendText("[ALERTA] Peticiones repetidas causarán latencia constante al usuario.\n");
                });
            }).start();
        });

        // --- 2. EVENTO PROXY ---
        btnProxy.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Consultando Proxy...");

            txtAuditoria.appendText("=== PRUEBA 2: PATRÓN PROXY (CONTROL Y CACHÉ) ===\n");
            txtAuditoria.appendText("[CLIENTE] Enviando petición a 'ReporteProxy'...\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();
                txtAuditoria.appendText("[PROXY] Evaluando mapa de memoria caché para el usuario '" + txtUsuario.getText() + "'...\n");

                Node reporte = proxyService.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    if (proxyService.isVieneDeCache()) {
                        mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "HIT CACHÉ");
                        txtAuditoria.appendText("[PROXY] RESULTADO: ¡Caché encontrada (HIT)!\n");
                        txtAuditoria.appendText("[PROXY] Se detiene la llamada a la BD real. Retorno instantáneo (" + (t2 - t1) + "ms).\n");
                    } else {
                        mostrarReporte(areaReporte, reporte, lblTiempo, (t2 - t1), lblEstado, "MISS CACHÉ");
                        txtAuditoria.appendText("[PROXY] RESULTADO: No está en memoria (MISS).\n");
                        txtAuditoria.appendText("[PROXY -> BD] Petición redirigida al servicio real (" + (t2 - t1) + "ms).\n");
                        txtAuditoria.appendText("[PROXY] Gráfico guardado exitosamente en caché para la próxima llamada.\n");
                    }
                });
            }).start();
        });

        // --- 3. EVENTO DECORATOR ---
        btnDecorator.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Aplicando Decoradores...");

            txtAuditoria.appendText("=== PRUEBA 3: PATRÓN DECORATOR (EXTENSIÓN EN CAPAS) ===\n");
            txtAuditoria.appendText("[CLIENTE] Construyendo envoltorio multinivel en tiempo de ejecución:\n");
            txtAuditoria.appendText("          -> WatermarkDecorador\n");
            txtAuditoria.appendText("             -> EncabezadoDecorador\n");
            txtAuditoria.appendText("                -> MarcoDecorador\n");
            txtAuditoria.appendText("                   -> ServicioBase (Gráfico puro)\n");

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
                    mostrarReporte(areaReporte, reporteFinal, lblTiempo, (t2 - t1), lblEstado, "Envoltorios Aplicados");
                    txtAuditoria.appendText("[BASE] 1. 'ReporteServiceImpl' entrega el gráfico primario.\n");
                    txtAuditoria.appendText("[DECORATOR] 2. 'MarcoDecorador' envuelve la vista con borde y sombra CSS.\n");
                    txtAuditoria.appendText("[DECORATOR] 3. 'EncabezadoDecorador' adjunta la cabecera confidencial superior.\n");
                    txtAuditoria.appendText("[DECORATOR] 4. 'WatermarkDecorador' superpone la marca de agua flotante.\n");
                    txtAuditoria.appendText("[AUDITORÍA] Extensibilidad cumplida: Ninguna clase conoce la implementación de las demás.\n");
                });
            }).start();
        });

        // --- 4. EVENTO SINERGIA TOTAL (PROXY + DECORATOR DETALLADO) ---
        btnSinergia.setOnAction(e -> {
            prepararEjecucion(areaReporte, lblEstado, txtAuditoria, "Sinergia Proxy + Decorator...");

            txtAuditoria.appendText("=== PRUEBA 4: COMBINACIÓN Y ARQUITECTURA (PROXY + DECORATOR) ===\n");
            txtAuditoria.appendText("[ARQUITECTURA] Cadena de Invocación:\n");
            txtAuditoria.appendText("   [Cliente] -> EncabezadoDecorador -> MarcoDecorador -> ReporteProxy -> ReporteServiceImpl\n");

            new Thread(() -> {
                long t1 = System.currentTimeMillis();

                // Construcción de la composición: Decoradores envolviendo al Proxy
                ReporteService servicioCombinado = new EncabezadoDecorador(
                        new MarcoDecorador(proxyService)
                );

                txtAuditoria.appendText("[EJECUCIÓN] Iniciando llamada desde la capa externa...\n");
                Node reporteCombinado = servicioCombinado.generarReporte(txtUsuario.getText());
                long t2 = System.currentTimeMillis();

                Platform.runLater(() -> {
                    boolean cacheHit = proxyService.isVieneDeCache();
                    String estadoMsg = cacheHit ? "SINERGIA: HIT DE CACHÉ + DECORACIÓN INSTANTÁNEA" : "SINERGIA: CARGA BASE + DECORACIÓN";

                    mostrarReporte(areaReporte, reporteCombinado, lblTiempo, (t2 - t1), lblEstado, estadoMsg);

                    txtAuditoria.appendText("\n--- FASE 1: CAPA PROXY (ACCESO Y OPTIMIZACIÓN) ---\n");
                    if (cacheHit) {
                        txtAuditoria.appendText("[PROXY] Status: HIT DE CACHÉ.\n");
                        txtAuditoria.appendText("[PROXY] Objeto gráfico recuperado de la memoria RAM en " + (t2 - t1) + " ms.\n");
                        txtAuditoria.appendText("[PROXY] Se omitió por completo el tiempo de espera de la Base de Datos.\n");
                    } else {
                        txtAuditoria.appendText("[PROXY] Status: MISS DE CACHÉ.\n");
                        txtAuditoria.appendText("[PROXY] No estaba en memoria. Se delega la generación pesada a 'ReporteServiceImpl'.\n");
                        txtAuditoria.appendText("[PROXY] El gráfico resultante fue almacenado en caché para llamadas futuras.\n");
                    }

                    txtAuditoria.appendText("\n--- FASE 2: CAPAS DECORATOR (TRANSFORMACIÓN VISUAL) ---\n");
                    txtAuditoria.appendText("[DECORATOR] 1. 'MarcoDecorador' recibió el gráfico (desde el Proxy) y le adosó borde/sombra CSS.\n");
                    txtAuditoria.appendText("[DECORATOR] 2. 'EncabezadoDecorador' tomó el resultado enmarcado y le insertó el membrete rojo superior.\n");

                    txtAuditoria.appendText("\n--- CONCLUSIÓN ARQUITECTÓNICA ---\n");
                    txtAuditoria.appendText("[ÉXITO] Proxy garantizó la velocidad de respuesta (" + (t2 - t1) + " ms) y Decorator la flexibilidad estética sin acoplamiento.");
                });
            }).start();
        });

        // Layout Principal
        HBox topBox = new HBox(8, new Label("Usuario:"), txtUsuario, btnBase, btnProxy, btnDecorator, btnSinergia);
        topBox.setAlignment(Pos.CENTER_LEFT);

        HBox metrics = new HBox(20, new Label("Tiempo respuesta:"), lblTiempo, new Label("Estado del sistema:"), lblEstado);

        VBox layout = new VBox(10, topBox, metrics, areaReporte, new Label("Auditor de Ejecución (Trazabilidad):"), txtAuditoria);
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