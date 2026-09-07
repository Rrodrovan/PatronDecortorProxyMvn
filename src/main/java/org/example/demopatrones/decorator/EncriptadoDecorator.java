package org.example.demopatrones.decorator;

import org.example.demopatrones.domain.ReporteService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.util.Base64;

public class EncriptadoDecorator extends ReporteDecorator {

    public EncriptadoDecorator(ReporteService servicio) {
        super(servicio);
    }

    @Override
    public Node generarReporte(String usuario) {
        // Obtiene el nodo gráfico del decorador anterior o servicio base
        Node graficoOriginal = super.generarReporte(usuario);

        // Genera un hash simulado para mostrar en pantalla
        String hashSimulado = Base64.getEncoder().encodeToString(usuario.getBytes());

        Label lblEncriptado = new Label("🔒 Firma Digital (Base64): " + hashSimulado);
        lblEncriptado.setStyle("-fx-font-family: monospace; -fx-font-size: 11px; -fx-text-fill: #7f8c8d;");

        VBox contenedor = new VBox(8, graficoOriginal, lblEncriptado);
        contenedor.setPadding(new Insets(5));
        return contenedor;
    }
}