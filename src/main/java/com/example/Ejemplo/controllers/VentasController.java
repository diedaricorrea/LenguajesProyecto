package com.example.Ejemplo.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@PreAuthorize("hasAnyAuthority('ESTADISTICAS_VER', 'VENTAS_VER', 'ROLE_ADMINISTRADOR')")
public class VentasController {

    @GetMapping("/ventas")
    public String ventas(Model model) {
        return "administrador/ventasGraf";
    }

    @GetMapping("/ventas2")
    public String ventas12(Model model) {
        return "administrador/ventas";
    }

}
