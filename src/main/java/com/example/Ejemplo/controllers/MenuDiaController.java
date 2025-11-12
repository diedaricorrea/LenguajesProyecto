package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.models.MenuDia;
import com.example.Ejemplo.models.Producto;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.impl.MenuDiaServiceImpl;
import com.example.Ejemplo.services.impl.ProductoServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/menuDia")
public class MenuDiaController {
    private final ProductoServiceImpl productosServiceImpl;
    private final MenuDiaServiceImpl menuDiaServiceImpl;

    @Autowired
    public MenuDiaController(ProductoServiceImpl productosServiceImpl, MenuDiaServiceImpl menuDiaServiceImpl) {
        this.productosServiceImpl = productosServiceImpl;
        this.menuDiaServiceImpl = menuDiaServiceImpl;
    }

    @GetMapping("/")
    public String menuDia(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        // Obtener productos de la categoría "MENU ECONOMICO" - convertir DTOs a Entidades
        Pageable unpaged = Pageable.unpaged();
        List<Producto> menusEconomicos = productosServiceImpl.buscarPorCategoriaYNombre("MENU ECONOMICO", "", unpaged)
            .map(dto -> {
                Producto p = new Producto();
                p.setIdProducto(dto.getIdProducto());
                p.setNombre(dto.getNombre());
                p.setDescripcion(dto.getDescripcion());
                p.setPrecio(dto.getPrecio());
                p.setStock(dto.getStock());
                p.setImagenUrl(dto.getImagenUrl());
                return p;
            }).getContent();
        
        model.addAttribute("menusEconomicos", menusEconomicos);
        model.addAttribute("menuDia", new MenuDia());
        Usuario usuario = userDetails.getUsuario();

        model.addAttribute("usuarioAdmins", usuario.getRol().toString());
        return "administrador/menuDia";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute MenuDia menuDia, RedirectAttributes redirectAttributes,
            BindingResult resultado) {
        if (resultado.hasErrors()) {
            return "administrador/menuDia";
        }
        menuDiaServiceImpl.saveMenudia(menuDia);
        redirectAttributes.addFlashAttribute("mensaje", "Menu Economico guardado correctamente");
        return "redirect:/menuDia";
    }
}
