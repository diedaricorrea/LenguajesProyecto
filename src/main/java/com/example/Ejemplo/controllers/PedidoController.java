package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.models.*;
import com.example.Ejemplo.services.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/pedidos")
public class PedidoController {
    private final PedidosServiceImpl pedidoServiceImpl;
    private final UsuarioServiceImpl usuarioServiceImpl;
    private final CarritoServiceImpl carritoServiceImpl;
    private final DetallePedidoServiceImpl detallePedidoServiceImpl;
    private final ProductoServiceImpl productoServiceImpl;
    private final NotificacionServiceImpl notificacionServiceImpl;

    @Autowired
    public PedidoController(PedidosServiceImpl pedidoServiceImpl, UsuarioServiceImpl usuarioServiceImpl, CarritoServiceImpl carritoServiceImpl, DetallePedidoServiceImpl detallePedidoServiceImpl, ProductoServiceImpl productoServiceImpl, NotificacionServiceImpl notificacionServiceImpl) {
        this.pedidoServiceImpl = pedidoServiceImpl;
        this.usuarioServiceImpl = usuarioServiceImpl;
        this.carritoServiceImpl = carritoServiceImpl;
        this.detallePedidoServiceImpl = detallePedidoServiceImpl;
        this.productoServiceImpl = productoServiceImpl;
        this.notificacionServiceImpl = notificacionServiceImpl;
    }

    @GetMapping()
    public String verPedidos(@AuthenticationPrincipal UsuarioDetails userDetails, Model model) {
        Usuario usuario = userDetails.getUsuario();
        int idUsuario = usuario.getIdUsuario();
        model.addAttribute("usuarioAdmins", usuario.getRol().toString());
        model.addAttribute("pedidos", pedidoServiceImpl.findByUsuario_Id(idUsuario));
        model.addAttribute("notificaciones",notificacionServiceImpl.findAllByUsuario_IdUsuario(idUsuario));
        model.addAttribute("pedidosDetalle", pedidoServiceImpl.obtenerDetallePedidosPorId(idUsuario));
        return "usuario/pedido";
    }

    @GetMapping("/admin")
    public String verPedidosPendientes(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();

        model.addAttribute("usuarioAdmins", usuario.getRol().toString());
        model.addAttribute("pedidosPendientes", pedidoServiceImpl.obtenerDetallePedidos());
        return "administrador/pendientes";
    }

    @GetMapping("/enviados")
    public String verPedidosEnviados(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("usuarioAdmins", usuario.getRol().toString());
        model.addAttribute("pedidosEnviados", pedidoServiceImpl.obtenerDetallePedidosEnviados());
        return "administrador/pedidosEnviados";
    }

    @PostMapping("/buscar")
    public String buscarPedido(Model model, @AuthenticationPrincipal UsuarioDetails userDetails, @RequestParam("codigo") String codigo) {
        Usuario usuario = userDetails.getUsuario();
        model.addAttribute("usuarioAdmins", usuario.getRol().toString());
        model.addAttribute("pedidosEnviados", pedidoServiceImpl.buscarPorCodigoPedido(codigo.toUpperCase()));
        return "administrador/pedidosEnviados";
    }

    @PostMapping("/pedir")
    public String pedir(@AuthenticationPrincipal UsuarioDetails userDetails, @RequestParam("horaEntrega") LocalTime horaEntrega, Model model) {
        try {
            // 1. Validar usuario
            Usuario usuario = userDetails.getUsuario();
            if (usuario == null) {
                throw new AuthenticationCredentialsNotFoundException("Usuario no autenticado");
            }

            int idUsuario = usuario.getIdUsuario();

            // 2. Obtener listas una sola vez
            List<Producto> productos = obtenerTodosProductos(idUsuario);
            List<Integer> cantidades = obtenerTodosProductosConCantidad(idUsuario);

            // 3. Validar que coincidan los tamaños
            if (productos.size() != cantidades.size()) {
                throw new IllegalStateException("Los productos y sus cantidades no coinciden");
            }

            // 4. Crear pedido
            Pedido pedido = new Pedido();
            pedido.setUsuario(usuarioServiceImpl.findUsuarioById(idUsuario).orElseThrow());
            pedido.setFechaEntrega(horaEntrega);
            pedido.setCodigoPedido(pedidoServiceImpl.generarCodigoUnico());
            Pedido pedidoGuardado = pedidoServiceImpl.guardarPedido(pedido);

            // 5. Procesar productos
            for (int i = 0; i < productos.size(); i++) {
                Producto pro = productos.get(i);
                int cantidad = cantidades.get(i); // Ahora sí accedes al índice correcto
                DetallePedido detallePedido = new DetallePedido();
                detallePedido.setPedido(pedidoGuardado);
                detallePedido.setProducto(pro);
                detallePedido.setCantidad(cantidad);
                detallePedido.setSubtotal(pro.getPrecio() * cantidad);

                DetallePedidoId detalleId = new DetallePedidoId(
                        pedidoGuardado.getIdPedido(),
                        pro.getIdProducto()
                );
                detallePedido.setDetallepedidoId(detalleId);

                productoServiceImpl.reducirStock(pro.getIdProducto(), cantidad);
                detallePedidoServiceImpl.saveDetallePedido(detallePedido);
            }

            carritoServiceImpl.limpiarCarrito(idUsuario);
            return "redirect:/pedidos";

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }


    List<Producto> obtenerTodosProductos(int idUsuario){
        return carritoServiceImpl.obtenerCarritosPorUsuario(idUsuario).stream().map(carrito -> carrito.getIdProducto()).collect(Collectors.toList());
    }
    List<Integer> obtenerTodosProductosConCantidad(int idUsuario){
        return carritoServiceImpl.obtenerCarritosPorUsuario(idUsuario).stream().map(carrito -> carrito.getCantidad()).collect(Collectors.toList());
    }

}
