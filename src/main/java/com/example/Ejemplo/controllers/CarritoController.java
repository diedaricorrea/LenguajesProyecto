package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.impl.CarritoServiceImpl;
import com.example.Ejemplo.services.impl.NotificacionServiceImpl;
import com.example.Ejemplo.services.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoServiceImpl carritoServiceImpl;
    private final NotificacionServiceImpl notificacionServiceImpl;

    @Autowired
    public CarritoController(CarritoServiceImpl carritoServiceImpl, NotificacionServiceImpl notificacionServiceImpl) {
        this.carritoServiceImpl = carritoServiceImpl;
        this.notificacionServiceImpl = notificacionServiceImpl;
    }

    @GetMapping()
    public String verCarrito(@AuthenticationPrincipal UsuarioDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();
        int idUsuario = usuario.getIdUsuario();
        model.addAttribute("usuarioAdmins", usuario.getRol().toString());
        model.addAttribute("notificaciones",notificacionServiceImpl.findAllByUsuario_IdUsuario(idUsuario));
        model.addAttribute("carrito", carritoServiceImpl.obtenerCarritosPorUsuario(idUsuario));
        return "usuario/carrito";
    }

    @PostMapping("/eliminar")
    public String eliminarDelCarrito(@AuthenticationPrincipal UsuarioDetails userDetails,
                                     @RequestParam int idProducto,
                                     RedirectAttributes redirectAttributes) {
        Usuario usuario = userDetails.getUsuario();
        int idUsuario = usuario.getIdUsuario();
        try {
            carritoServiceImpl.eliminarProductoAgregado(idUsuario,idProducto);
            redirectAttributes.addFlashAttribute("success", "Producto eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el producto: " + e.getMessage());
        }

        return "redirect:/carrito";
    }

}