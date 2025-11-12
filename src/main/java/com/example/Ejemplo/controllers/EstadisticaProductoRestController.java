package com.example.Ejemplo.controllers;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.Ejemplo.repository.DetalleVentaRepository;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticaProductoRestController {

    private final DetalleVentaRepository detalleVentaRepository;

    @Autowired
    public EstadisticaProductoRestController(DetalleVentaRepository detalleVentaRepository) {
        this.detalleVentaRepository = detalleVentaRepository;
    }


    @GetMapping("/masVendidos")
    public List<?> getProductosMasVendidos() {
        return detalleVentaRepository.findProductosMasVendidos();
    }
}
