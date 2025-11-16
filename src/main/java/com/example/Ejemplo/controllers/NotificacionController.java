package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.impl.NotificacionServiceImpl;
import com.example.Ejemplo.services.impl.PedidosServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequestMapping("/notificacion")
public class NotificacionController {

    private final NotificacionServiceImpl notificacionServiceImpl;

    private final PedidosServiceImpl pedidosServiceImpl;

    public NotificacionController(NotificacionServiceImpl notificacionServiceImpl, PedidosServiceImpl pedidosServiceImpl) {
        this.notificacionServiceImpl = notificacionServiceImpl;
        this.pedidosServiceImpl = pedidosServiceImpl;
    }

    @PostMapping("/notificar")
    public String notificar(@RequestParam("idUsuario") int idUsuario, @RequestParam("codigoPedido") String codigoPedido, Model model) {
        String message = "Tu pedido esta listo";
        //envio de notificacion al usuario
        notificacionServiceImpl.sendNotificacion(idUsuario, message);
        //eliminacion del pedido de la lista de pendientes del usuario
        pedidosServiceImpl.eliminarPedido(codigoPedido);
        return "redirect:/admin/pedidos";
    }

}
