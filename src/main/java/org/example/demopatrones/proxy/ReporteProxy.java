package org.example.demopatrones.proxy;

import org.example.demopatrones.domain.ReporteService;
import javafx.scene.Node;
import java.util.HashMap;
import java.util.Map;

public class ReporteProxy implements ReporteService {
    private final ReporteService servicioReal;
    private final Map<String, Node> cache = new HashMap<>();
    private boolean vieneDeCache = false;

    public ReporteProxy(ReporteService servicioReal) {
        this.servicioReal = servicioReal;
    }

    @Override
    public Node generarReporte(String usuario) {
        if (cache.containsKey(usuario)) {
            this.vieneDeCache = true;
            return cache.get(usuario);
        }

        this.vieneDeCache = false;
        Node reporteReal = servicioReal.generarReporte(usuario);
        cache.put(usuario, reporteReal);
        return reporteReal;
    }

    public boolean isVieneDeCache() {
        return vieneDeCache;
    }
}