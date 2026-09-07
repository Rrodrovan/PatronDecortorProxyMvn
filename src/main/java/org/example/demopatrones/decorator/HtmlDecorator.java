package org.example.demopatrones.decorator;

import org.example.demopatrones.domain.ReporteService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.VBox;

public class HtmlDecorator extends ReporteDecorator {

    public HtmlDecorator(ReporteService servicio) {
        super(servicio);
    }

    @Override
    public Node generarReporte(String usuario) {
        Node graficoOriginal = super.generarReporte(usuario);

        // Envoltorio estilo Web (Card HTML)
        VBox tarjetaHtml = new VBox(graficoOriginal);
        tarjetaHtml.setPadding(new Insets(15));
        tarjetaHtml.setStyle(
                "-fx-background-color: #ffffff; " +
                        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5); " +
                        "-fx-background-radius: 10px;"
        );

        return tarjetaHtml;
    }
}