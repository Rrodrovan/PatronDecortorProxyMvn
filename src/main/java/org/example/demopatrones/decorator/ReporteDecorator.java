package org.example.demopatrones.decorator;

import org.example.demopatrones.domain.ReporteService;
import javafx.scene.Node;

public abstract class ReporteDecorator implements ReporteService {
    protected final ReporteService servicioDecorado;

    public ReporteDecorator(ReporteService servicio) {
        this.servicioDecorado = servicio;
    }

    @Override
    public Node generarReporte(String usuario) {
        return servicioDecorado.generarReporte(usuario);
    }
}