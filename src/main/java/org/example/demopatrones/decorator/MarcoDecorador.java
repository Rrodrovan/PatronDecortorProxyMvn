package org.example.demopatrones.decorator;

import org.example.demopatrones.domain.ReporteService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class MarcoDecorador extends ReporteDecorator {
    public MarcoDecorador(ReporteService servicio) {
        super(servicio);
    }

    @Override
    public Node generarReporte(String usuario) {
        Node graficoOriginal = super.generarReporte(usuario);

        VBox contenedorConMarco = new VBox(graficoOriginal);
        contenedorConMarco.setPadding(new Insets(10));
        contenedorConMarco.setStyle(
                "-fx-border-color: #0275d8; " +
                        "-fx-border-width: 3px; " +
                        "-fx-border-radius: 8px; " +
                        "-fx-background-color: #f8f9fa;"
        );
        return contenedorConMarco;
    }
}