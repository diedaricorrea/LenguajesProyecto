package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.models.MenuDia;
import com.example.Ejemplo.models.Producto;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.repository.ProductoRepository;
import com.example.Ejemplo.services.impl.MenuDiaServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/menuDia")
@PreAuthorize("hasAnyAuthority('MENU_DIA_VER', 'MENU_DIA_GESTIONAR', 'ROLE_ADMINISTRADOR')")
public class MenuDiaController {
    private final ProductoRepository productoRepository;
    private final MenuDiaServiceImpl menuDiaServiceImpl;

    @Autowired
    public MenuDiaController(ProductoRepository productoRepository, MenuDiaServiceImpl menuDiaServiceImpl) {
        this.productoRepository = productoRepository;
        this.menuDiaServiceImpl = menuDiaServiceImpl;
    }

    @GetMapping("/")
    public String menuDia(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        // Obtener TODOS los productos activos directamente del repositorio
        List<Producto> menusEconomicos = productoRepository.findAll();
        
        // Obtener menús programados (ordenados por fecha descendente)
        List<MenuDia> menusProgramados = menuDiaServiceImpl.findAllOrderByFechaDesc();
        
        // Obtener menús de HOY
        List<MenuDia> menusHoy = menuDiaServiceImpl.findMenusDelDia(LocalDate.now());
        
        model.addAttribute("menusEconomicos", menusEconomicos);
        model.addAttribute("menusProgramados", menusProgramados);
        model.addAttribute("menusHoy", menusHoy);
        model.addAttribute("menuDia", new MenuDia());
        Usuario usuario = userDetails.getUsuario();

        model.addAttribute("usuarioAdmins", usuario.getRol().toString());
        return "administrador/menuDia";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute MenuDia menuDia, RedirectAttributes redirectAttributes,
            BindingResult resultado) {
        if (resultado.hasErrors()) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el menú");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/menuDia/";
        }
        menuDiaServiceImpl.saveMenudia(menuDia);
        redirectAttributes.addFlashAttribute("mensaje", "Menú programado correctamente");
        redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        return "redirect:/menuDia/";
    }
    
    @PostMapping("/{id}/eliminar")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            menuDiaServiceImpl.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Menú eliminado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar el menú");
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        return "redirect:/menuDia/";
    }
}
