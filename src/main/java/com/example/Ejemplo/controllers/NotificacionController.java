package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.impl.NotificacionServiceImpl;
import com.example.Ejemplo.services.impl.PedidosServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notificacion")
@RequiredArgsConstructor
@Slf4j
public class NotificacionController {

    private final NotificacionServiceImpl notificacionServiceImpl;
    private final PedidosServiceImpl pedidosServiceImpl;

    @PostMapping("/notificar")
    public String notificar(@RequestParam("idUsuario") int idUsuario, @RequestParam("codigoPedido") String codigoPedido, Model model) {
        String message = "Tu pedido esta listo";
        //envio de notificacion al usuario
        notificacionServiceImpl.sendNotificacion(idUsuario, message);
        //eliminacion del pedido de la lista de pendientes del usuario
        pedidosServiceImpl.eliminarPedido(codigoPedido);
        return "redirect:/admin/pedidos";
    }
    
    /**
     * Elimina una notificación específica
     */
    @PostMapping("/eliminar")
    public String eliminarNotificacion(
            @RequestParam("idNotificacion") int idNotificacion,
            @RequestParam(value = "returnUrl", required = false, defaultValue = "/catalogo") String returnUrl,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        try {
            Usuario usuario = userDetails.getUsuario();
            log.info("Eliminando notificación {} para usuario {}", idNotificacion, usuario.getIdUsuario());
            notificacionServiceImpl.deleteNotificacion(idNotificacion, usuario.getIdUsuario());
            log.info("Redirigiendo a: {}", returnUrl);
        } catch (Exception e) {
            log.error("Error al eliminar notificación: ", e);
        }
        return "redirect:" + returnUrl;
    }
    
    /**
     * Elimina todas las notificaciones del usuario
     */
    @PostMapping("/eliminar-todas")
    public String eliminarTodasNotificaciones(
            @RequestParam(value = "returnUrl", required = false, defaultValue = "/catalogo") String returnUrl,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = userDetails.getUsuario();
            log.info("Eliminando todas las notificaciones para usuario {}", usuario.getIdUsuario());
            notificacionServiceImpl.deleteAllNotificaciones(usuario.getIdUsuario());
            log.info("Todas las notificaciones eliminadas. Redirigiendo a: {}", returnUrl);
        } catch (Exception e) {
            log.error("Error al eliminar todas las notificaciones: ", e);
        }
        return "redirect:" + returnUrl;
    }

}
