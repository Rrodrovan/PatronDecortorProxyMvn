package org.example.demopatrones.service;

import org.example.demopatrones.domain.ReporteService;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

public class ReporteServiceImpl implements ReporteService {
    @Override
    public Node generarReporte(String usuario) {
        try {
            Thread.sleep(1500); // Simula proceso pesado de BD
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Meses");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ventas ($)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Reporte Financiero de Ventas - " + usuario);
        barChart.setLegendVisible(false);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.getData().add(new XYChart.Data<>("Enero", 12000));
        series.getData().add(new XYChart.Data<>("Febrero", 19000));
        series.getData().add(new XYChart.Data<>("Marzo", 15000));
        series.getData().add(new XYChart.Data<>("Abril", 24000));

        barChart.getData().add(series);
        return barChart;
    }
}