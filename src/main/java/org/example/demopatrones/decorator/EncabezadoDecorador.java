package org.example.demopatrones.decorator;

import org.example.demopatrones.domain.ReporteService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class EncabezadoDecorador extends ReporteDecorator {
    public EncabezadoDecorador(ReporteService servicio) {
        super(servicio);
    }

    @Override
    public Node generarReporte(String usuario) {
        Node graficoOriginal = super.generarReporte(usuario);

        Label lblMembrete = new Label("CONFIDENCIAL - USO INTERNO EXCLUSIVO");
        lblMembrete.setStyle("-fx-font-weight: bold; -fx-text-fill: #d9534f; -fx-font-size: 14px;");

        VBox contenedor = new VBox(10, lblMembrete, graficoOriginal);
        contenedor.setPadding(new Insets(5));
        return contenedor;
    }
}