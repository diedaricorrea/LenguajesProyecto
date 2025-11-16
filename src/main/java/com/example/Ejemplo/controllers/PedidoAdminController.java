package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.PedidoDTO;
import com.example.Ejemplo.models.EstadoPedido;
import com.example.Ejemplo.models.Pedido;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.PedidosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestión administrativa de pedidos
 * Gestiona los pedidos desde el punto de vista de la cafetería
 * 
 * Rutas principales:
 * - GET /admin/pedidos - Lista todos los pedidos
 * - POST /admin/pedidos/buscar - Busca por código
 * - POST /admin/pedidos/{id}/avanzar - Avanza al siguiente estado
 * - POST /admin/pedidos/{id}/cancelar - Cancela un pedido
 */
@Controller
@RequestMapping("/admin/pedidos")
@Slf4j
@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
public class PedidoAdminController {
    
    private final PedidosService pedidosService;
    
    public PedidoAdminController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }
    
    /**
     * Lista todos los pedidos agrupados por estado
     * GET /admin/pedidos
     */
    @GetMapping
    public String listarPedidos(
            @AuthenticationPrincipal UsuarioDetails userDetails,
            Model model) {
        try {
            Usuario usuario = userDetails.getUsuario();
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            
            // Obtener pedidos agrupados por estado (lógica delegada al service)
            Map<EstadoPedido, List<PedidoDTO>> pedidosPorEstado = pedidosService.agruparPedidosPorEstado();
            
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("pedidosPendientes", pedidosPorEstado.getOrDefault(EstadoPedido.PENDIENTE, List.of()));
            model.addAttribute("pedidosEnPreparacion", pedidosPorEstado.getOrDefault(EstadoPedido.EN_PREPARACION, List.of()));
            model.addAttribute("pedidosListos", pedidosPorEstado.getOrDefault(EstadoPedido.LISTO, List.of()));
            model.addAttribute("pedidosEntregados", pedidosPorEstado.getOrDefault(EstadoPedido.ENTREGADO, List.of()));
            model.addAttribute("pedidosCancelados", pedidosPorEstado.getOrDefault(EstadoPedido.CANCELADO, List.of()));
            
            int totalPedidos = pedidosPorEstado.values().stream()
                    .mapToInt(List::size)
                    .sum();
            model.addAttribute("totalPedidos", totalPedidos);
            
            log.debug("Listando pedidos: Total={}", totalPedidos);
            
            return "administrador/pedidosLista";
            
        } catch (Exception e) {
            log.error("Error al listar pedidos", e);
            model.addAttribute("error", "Error al cargar los pedidos: " + e.getMessage());
            return "error/error";
        }
    }
    
    /**
     * Busca pedidos por código
     * POST /admin/pedidos/buscar
     */
    @PostMapping("/buscar")
    public String buscarPedido(
            @RequestParam("codigo") String codigo,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            Model model) {
        try {
            Usuario usuario = userDetails.getUsuario();
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            
            List<Pedido> pedidos = pedidosService.buscarPorCodigoPedido(codigo.toUpperCase());
            List<PedidoDTO> pedidosDTO = pedidosService.convertirPedidosADTO(pedidos);
            
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("pedidosEncontrados", pedidosDTO);
            model.addAttribute("codigoBuscado", codigo.toUpperCase());
            
            if (pedidosDTO.isEmpty()) {
                model.addAttribute("mensaje", "No se encontraron pedidos con el código: " + codigo);
                model.addAttribute("tipoMensaje", "warning");
            }
            
            return "administrador/pedidosLista";
            
        } catch (Exception e) {
            log.error("Error al buscar pedido: {}", codigo, e);
            model.addAttribute("error", "Error al buscar el pedido");
            return "error/error";
        }
    }
    
    /**
     * Avanza un pedido al siguiente estado del flujo
     * POST /admin/pedidos/{id}/avanzar
     */
    @PostMapping("/{id}/avanzar")
    public String avanzarEstado(
            @PathVariable Integer id,
            @RequestParam(required = false) String fromTab,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el usuario del pedido para notificarle
            Pedido pedido = pedidosService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
            
            Integer idUsuario = pedido.getUsuario() != null ? pedido.getUsuario().getIdUsuario() : null;
            
            // Delegar la lógica al service
            if (!pedidosService.avanzarEstadoPedido(id, idUsuario)) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "No se puede avanzar el estado del pedido");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            }
            // No mostramos mensaje de éxito para no duplicar con la confirmación
            
            // Recordar el tab desde donde se hizo la acción
            if (fromTab != null) {
                redirectAttributes.addFlashAttribute("activeTab", fromTab);
            }
            
        } catch (Exception e) {
            log.error("Error al avanzar estado del pedido: {}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al actualizar el pedido: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/pedidos";
    }
    
    /**
     * Cancela un pedido
     * POST /admin/pedidos/{id}/cancelar
     */
    @PostMapping("/{id}/cancelar")
    public String cancelarPedido(
            @PathVariable Integer id,
            @RequestParam(required = false) String motivo,
            @RequestParam(required = false) String fromTab,
            RedirectAttributes redirectAttributes) {
        try {
            // Obtener el usuario del pedido para notificarle
            Pedido pedido = pedidosService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado"));
            
            Integer idUsuario = pedido.getUsuario() != null ? pedido.getUsuario().getIdUsuario() : null;
            
            // Delegar la lógica al service
            if (pedidosService.cancelarPedido(id, motivo, idUsuario)) {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "Pedido cancelado exitosamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "info");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", 
                    "No se puede cancelar el pedido en su estado actual");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            }
            
            // Recordar el tab desde donde se hizo la acción
            if (fromTab != null) {
                redirectAttributes.addFlashAttribute("activeTab", fromTab);
            }
            
        } catch (Exception e) {
            log.error("Error al cancelar pedido: {}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", 
                "Error al cancelar el pedido: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/pedidos";
    }
}
