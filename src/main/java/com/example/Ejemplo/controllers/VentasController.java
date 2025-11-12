package com.example.Ejemplo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
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
