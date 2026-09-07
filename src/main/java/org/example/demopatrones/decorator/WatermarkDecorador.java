package org.example.demopatrones.decorator;

import org.example.demopatrones.domain.ReporteService;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class WatermarkDecorador extends ReporteDecorator {

    public WatermarkDecorador(ReporteService servicio) {
        super(servicio);
    }

    @Override
    public Node generarReporte(String usuario) {
        // Obtiene el nodo procesado por las capas internas
        Node contenidoAnterior = super.generarReporte(usuario);

        // Crea una marca de agua translúcida
        Label lblWatermark = new Label("DRAFT - PROPIEDAD INTELECTUAL");
        lblWatermark.setStyle(
                "-fx-font-size: 22px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-text-fill: rgba(200, 0, 0, 0.18); " +
                        "-fx-rotate: -20;"
        );

        StackPane panelOverlay = new StackPane(contenidoAnterior, lblWatermark);
        StackPane.setAlignment(lblWatermark, Pos.CENTER);
        return panelOverlay;
    }
}