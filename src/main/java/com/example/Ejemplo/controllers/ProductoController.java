package com.example.Ejemplo.controllers;

import java.util.List;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.UsuarioRegistroDTO;
import com.example.Ejemplo.models.Carrito;
import com.example.Ejemplo.models.Producto;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.impl.CarritoServiceImpl;
import com.example.Ejemplo.services.impl.NotificacionServiceImpl;
import com.example.Ejemplo.services.impl.ProductoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/catalogo")
public class ProductoController {

    private final ProductoServiceImpl productosServiceImpl;

    private final CarritoServiceImpl carritoServiceImpl;

    private final NotificacionServiceImpl notificacionServiceImpl;

    @Autowired
    public ProductoController(ProductoServiceImpl productosServiceImpl, CarritoServiceImpl carritoServiceImpl, NotificacionServiceImpl notificacionServiceImpl) {
        this.productosServiceImpl = productosServiceImpl;
        this.carritoServiceImpl = carritoServiceImpl;
        this.notificacionServiceImpl = notificacionServiceImpl;
    }

    @GetMapping()
    public String index(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            Model model) {
        Usuario usuario = userDetails != null ? userDetails.getUsuario() : null;

        // Solo agregar datos de usuario si está autenticado
        if (usuario != null) {
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("usuarioNombre", usuario.getNombre());
            model.addAttribute("notificaciones", notificacionServiceImpl.findAllByUsuario_IdUsuario(usuario.getIdUsuario()));
        } else {
            model.addAttribute("usuarioAdmins", "INVITADO");
            model.addAttribute("usuarioNombre", "Invitado");
        }
        
        // Agregar objeto para el formulario de registro (modales)
        if (!model.containsAttribute("usuarioRegistro")) {
            model.addAttribute("usuarioRegistro", new UsuarioRegistroDTO());
        }

        int pageSize = 6;
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Producto> productosPage;

        if ((categoria != null && !categoria.isEmpty()) || (busqueda != null && !busqueda.isEmpty())) {
            productosPage = productosServiceImpl.buscarPorCategoriaYNombre(categoria, busqueda, pageable).map(dto -> {
                Producto p = new Producto();
                p.setIdProducto(dto.getIdProducto());
                p.setNombre(dto.getNombre());
                p.setDescripcion(dto.getDescripcion());
                p.setPrecio(dto.getPrecio());
                p.setStock(dto.getStock());
                p.setImagenUrl(dto.getImagenUrl());
                return p;
            });
        } else {
            productosPage = productosServiceImpl.obtenerTodosPaginado(pageable).map(dto -> {
                Producto p = new Producto();
                p.setIdProducto(dto.getIdProducto());
                p.setNombre(dto.getNombre());
                p.setDescripcion(dto.getDescripcion());
                p.setPrecio(dto.getPrecio());
                p.setStock(dto.getStock());
                p.setImagenUrl(dto.getImagenUrl());
                return p;
            });
        }

        if (busqueda != null && !busqueda.isEmpty() && productosPage.isEmpty()) {
            model.addAttribute("noResultados", "Lo sentimos, no pudimos encontrar ese producto.");
        }
        
        model.addAttribute("productos", productosPage.getContent());
        model.addAttribute("productosPage", productosPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productosPage.getTotalPages());
        model.addAttribute("categoriaActual", categoria);
        model.addAttribute("busquedaActual", busqueda);
        model.addAttribute("noResultados", productosPage.getContent().isEmpty());

        return "usuario/catalogo";
    }

    @PostMapping("/subir")
    @PreAuthorize("hasAnyAuthority('CARRITO_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_USUARIO', 'ROLE_TRABAJADOR')")
    public String subirAlCarrito(
            @RequestParam Integer idProducto,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            @RequestParam int cantidad,
            RedirectAttributes redirectAttributes) {
        Usuario usuario = userDetails.getUsuario();
        int idUsuario = usuario.getIdUsuario();
        int nuevaCantidad = 0;
        double nuevoTotal = 0;
        Producto producto = productosServiceImpl.buscarPorId(idProducto).map(dto -> {
            Producto p = new Producto();
            p.setIdProducto(dto.getIdProducto());
            p.setNombre(dto.getNombre());
            p.setPrecio(dto.getPrecio());
            p.setStock(dto.getStock());
            return p;
        }).orElse(null);

        if (producto == null) {
            redirectAttributes.addFlashAttribute("message", "Producto no encontrado.");
            return "redirect:/catalogo";
        }

        if (cantidad > producto.getStock()) {
            redirectAttributes.addFlashAttribute("message", "El stock no puede ser menor que el stock.");
            return "redirect:/catalogo";
        }

        double total = cantidad * producto.getPrecio();
        List<Carrito> carrito = carritoServiceImpl.obtenerCarritosPorUsuario(idUsuario);
        for (Carrito carro : carrito) {
            if (carro.getIdProducto().getIdProducto() == idProducto) {
                nuevaCantidad = carro.getCantidad() + cantidad;
                nuevoTotal = nuevaCantidad * carro.getIdProducto().getPrecio();
                if (carritoServiceImpl.actualizarProductoAgregado(idUsuario, idProducto, nuevaCantidad,
                        nuevoTotal) > 0) {
                    redirectAttributes.addFlashAttribute("message", "Se añadió al carrito correctamente");
                    return "redirect:/catalogo";
                }
            }
        }

        carritoServiceImpl.saveCarrito(idUsuario, idProducto, cantidad, total);
        redirectAttributes.addFlashAttribute("message", "Se añadió al carrito correctamente");
        return "redirect:/catalogo";
    }


}