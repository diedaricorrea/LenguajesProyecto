package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.PedidoDTO;
import com.example.Ejemplo.models.EstadoPedido;
import com.example.Ejemplo.models.Pedido;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.PedidosService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
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
@PreAuthorize("hasAnyAuthority('PEDIDOS_VER', 'PEDIDOS_GESTIONAR', 'ROLE_ADMINISTRADOR')")
@Slf4j
public class PedidoAdminController {
    
    private final PedidosService pedidosService;
    
    public PedidoAdminController(PedidosService pedidosService) {
        this.pedidosService = pedidosService;
    }
    
    /**
     * Lista todos los pedidos agrupados por estado
     * Soporta filtro por rango de fechas
     * GET /admin/pedidos
     */
    @GetMapping
    public String listarPedidos(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            @RequestParam(required = false) String filtro,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            Model model) {
        try {
            Usuario usuario = userDetails.getUsuario();
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            
            Map<EstadoPedido, List<PedidoDTO>> pedidosPorEstado;
            String periodoTexto = "Todos los pedidos";
            
            // Aplicar filtros predefinidos
            if (filtro != null) {
                LocalDate hoy = LocalDate.now();
                switch (filtro) {
                    case "hoy":
                        fechaInicio = hoy;
                        fechaFin = hoy;
                        periodoTexto = "Pedidos de hoy";
                        break;
                    case "semana":
                        fechaInicio = hoy.minusDays(hoy.getDayOfWeek().getValue() - 1);
                        fechaFin = hoy;
                        periodoTexto = "Pedidos de esta semana";
                        break;
                    case "mes":
                        fechaInicio = hoy.withDayOfMonth(1);
                        fechaFin = hoy;
                        periodoTexto = "Pedidos de este mes";
                        break;
                }
            }
            
            // Obtener pedidos según filtro de fecha
            if (fechaInicio != null && fechaFin != null) {
                pedidosPorEstado = pedidosService.agruparPedidosPorEstadoYFecha(fechaInicio, fechaFin);
                if (filtro == null) {
                    periodoTexto = "Pedidos del " + fechaInicio + " al " + fechaFin;
                }
            } else {
                pedidosPorEstado = pedidosService.agruparPedidosPorEstado();
            }
            
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
            model.addAttribute("periodoTexto", periodoTexto);
            model.addAttribute("fechaInicio", fechaInicio);
            model.addAttribute("fechaFin", fechaFin);
            model.addAttribute("filtroActivo", filtro);
            
            // Calcular estadísticas del período
            double totalVentas = pedidosPorEstado.values().stream()
                    .flatMap(List::stream)
                    .filter(p -> p.getEstado() != EstadoPedido.CANCELADO)
                    .mapToDouble(PedidoDTO::getTotal)
                    .sum();
            
            int pedidosCompletados = pedidosPorEstado.getOrDefault(EstadoPedido.ENTREGADO, List.of()).size();
            int pedidosCancelados = pedidosPorEstado.getOrDefault(EstadoPedido.CANCELADO, List.of()).size();
            double promedioVenta = totalPedidos > 0 ? totalVentas / totalPedidos : 0;
            
            model.addAttribute("totalVentas", totalVentas);
            model.addAttribute("pedidosCompletados", pedidosCompletados);
            model.addAttribute("pedidosCanceladosCount", pedidosCancelados);
            model.addAttribute("promedioVenta", promedioVenta);
            
            log.debug("Listando pedidos: Total={}, Período={}", totalPedidos, periodoTexto);
            
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
