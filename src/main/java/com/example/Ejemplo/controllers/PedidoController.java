package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.PedidoDTO;
import com.example.Ejemplo.models.*;
import com.example.Ejemplo.services.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidosService pedidosService;
    private final UsuarioService usuarioService;
    private final CarritoService carritoService;
    private final DetallePedidoService detallePedidoService;
    private final ProductoService productoService;
    private final NotificacionService notificacionService;

    public PedidoController(PedidosService pedidosService, 
                           UsuarioService usuarioService,
                           CarritoService carritoService, 
                           DetallePedidoService detallePedidoService,
                           ProductoService productoService, 
                           NotificacionService notificacionService) {
        this.pedidosService = pedidosService;
        this.usuarioService = usuarioService;
        this.carritoService = carritoService;
        this.detallePedidoService = detallePedidoService;
        this.productoService = productoService;
        this.notificacionService = notificacionService;
    }


    @GetMapping()
    public String verPedidos(@AuthenticationPrincipal UsuarioDetails userDetails, Model model) {
        try {
            Usuario usuario = userDetails.getUsuario();
            int idUsuario = usuario.getIdUsuario();
            

            List<Pedido> pedidos = pedidosService.obtenerPedidosPorUsuario(idUsuario);
            List<PedidoDTO> pedidosDTO = pedidosService.convertirPedidosADTO(pedidos);
            

            List<PedidoDTO> pedidosActivos = pedidosDTO.stream()
                    .filter(p -> !p.getEstado().esFinal())
                    .collect(Collectors.toList());
            
            List<PedidoDTO> pedidosCompletados = pedidosDTO.stream()
                    .filter(p -> p.getEstado().esFinal())
                    .collect(Collectors.toList());
            
            model.addAttribute("usuarioAdmins", usuario.getRol().toString());
            model.addAttribute("pedidosActivos", pedidosActivos);
            model.addAttribute("pedidosCompletados", pedidosCompletados);
            model.addAttribute("notificaciones", notificacionService.findAllByUsuario_IdUsuario(idUsuario));
            
            log.debug("Mostrando pedidos del usuario {}: {} activos, {} completados", 
                     idUsuario, pedidosActivos.size(), pedidosCompletados.size());
            
            return "usuario/pedido";
            
        } catch (Exception e) {
            log.error("Error al cargar pedidos del usuario", e);
            model.addAttribute("error", "Error al cargar tus pedidos: " + e.getMessage());
            return "error/error";
        }
    }


    @PostMapping("/pedir")
    public String pedir(@AuthenticationPrincipal UsuarioDetails userDetails, 
                       @RequestParam("horaEntrega") LocalTime horaEntrega, 
                       Model model) {
        try {
            // 1. Validar usuario
            Usuario usuario = userDetails.getUsuario();
            if (usuario == null) {
                throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
            }

            int idUsuario = usuario.getIdUsuario();

            // 2. Obtener productos del carrito
            List<Producto> productos = obtenerTodosProductos(idUsuario);
            List<Integer> cantidades = obtenerTodosProductosConCantidad(idUsuario);

            // 3. Validar que haya productos en el carrito
            if (productos.isEmpty()) {
                model.addAttribute("error", "El carrito está vacío");
                return "redirect:/carrito";
            }

            // 4. Validar que coincidan los tamaños
            if (productos.size() != cantidades.size()) {
                throw new IllegalStateException("Los productos y sus cantidades no coinciden");
            }

            // 5. Crear pedido con estado inicial PENDIENTE
            Pedido pedido = new Pedido();
            
            // Buscar entidad Usuario usando método legacy del service
            Usuario usuarioEntity = usuarioService.findUsuarioEntityById(idUsuario);
            if (usuarioEntity == null) {
                throw new IllegalStateException("Usuario no encontrado");
            }
            
            pedido.setUsuario(usuarioEntity);
            pedido.setFechaEntrega(horaEntrega);
            pedido.setCodigoPedido(pedidosService.generarCodigoUnico());
            pedido.setEstado(EstadoPedido.PENDIENTE); // Estado inicial
            
            Pedido pedidoGuardado = pedidosService.guardarPedido(pedido);

            // 6. Procesar detalles del pedido
            for (int i = 0; i < productos.size(); i++) {
                Producto producto = productos.get(i);
                int cantidad = cantidades.get(i);
                
                // Validar stock disponible
                if (producto.getStock() < cantidad) {
                    throw new IllegalStateException(
                        String.format("Stock insuficiente para %s. Disponible: %d, Solicitado: %d", 
                                     producto.getNombre(), producto.getStock(), cantidad)
                    );
                }
                
                DetallePedido detallePedido = new DetallePedido();
                detallePedido.setPedido(pedidoGuardado);
                detallePedido.setProducto(producto);
                detallePedido.setCantidad(cantidad);
                detallePedido.setSubtotal(producto.getPrecio() * cantidad);

                DetallePedidoId detalleId = new DetallePedidoId(
                        pedidoGuardado.getIdPedido(),
                        producto.getIdProducto()
                );
                detallePedido.setDetallepedidoId(detalleId);

                // Reducir stock
                productoService.reducirStock(producto.getIdProducto(), cantidad);
                detallePedidoService.saveDetallePedido(detallePedido);
            }

            // 7. Limpiar carrito
            carritoService.limpiarCarrito(idUsuario);
            
            // 8. Crear notificación de confirmación
            notificacionService.crearNotificacion(
                idUsuario,
                String.format("¡Pedido %s creado exitosamente! Te notificaremos cuando esté listo.", 
                            pedidoGuardado.getCodigoPedido())
            );
            
            log.info("Pedido {} creado exitosamente para usuario {}", 
                    pedidoGuardado.getCodigoPedido(), idUsuario);
            
            return "redirect:/pedidos";

        } catch (IllegalStateException e) {
            log.error("Error de validación al crear pedido", e);
            model.addAttribute("error", e.getMessage());
            return "redirect:/carrito?error=" + e.getMessage();
        } catch (Exception e) {
            log.error("Error al crear pedido", e);
            model.addAttribute("error", "Error al procesar tu pedido: " + e.getMessage());
            return "error/error";
        }
    }


    private List<Producto> obtenerTodosProductos(int idUsuario) {
        return carritoService.obtenerCarritosPorUsuario(idUsuario).stream()
                .map(Carrito::getIdProducto)
                .collect(Collectors.toList());
    }

    private List<Integer> obtenerTodosProductosConCantidad(int idUsuario) {
        return carritoService.obtenerCarritosPorUsuario(idUsuario).stream()
                .map(Carrito::getCantidad)
                .collect(Collectors.toList());
    }
}
