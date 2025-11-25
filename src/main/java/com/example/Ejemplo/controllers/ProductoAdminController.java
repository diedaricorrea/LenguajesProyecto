package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.CategoriaService;
import com.example.Ejemplo.services.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/productos")
@PreAuthorize("hasAnyAuthority('PRODUCTOS_VER', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR')")
@RequiredArgsConstructor
@Slf4j
public class ProductoAdminController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_GESTIONAR', 'PRODUCTOS_VER', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String listarProductos(
            @RequestParam(required = false) String busqueda,
            @RequestParam(required = false) String estado,
            Model model, 
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        try {
            Usuario usuario = userDetails.getUsuario();
            log.info("Usuario {} accediendo a gestion de productos", usuario.getCorreo());

            List<ProductoResponseDTO> productos = productoService.obtenerTodosConDetalles();
            
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                productos = productos.stream()
                    .filter(p -> p.getNombre().toLowerCase().contains(busqueda.toLowerCase()))
                    .toList();
            }
            
            if (estado != null && !estado.isEmpty()) {
                boolean estadoBoolean = "activo".equalsIgnoreCase(estado);
                productos = productos.stream()
                    .filter(p -> p.getEstado().equals(estadoBoolean))
                    .toList();
            }
            
            List<CategoriaDTO> categorias = categoriaService.obtenerTodas();
            
            log.debug("Listando productos. Total: {}", productos.size());

            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("productos", productos);
            model.addAttribute("categorias", categorias);
            model.addAttribute("busqueda", busqueda);
            model.addAttribute("estadoFiltro", estado);
            
            return "administrador/productosLista";
        } catch (Exception e) {
            log.error("Error al listar productos: ", e);
            throw e;
        }
    }


    @GetMapping("/nuevo")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_CREAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String nuevoProducto(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        List<CategoriaDTO> categorias = categoriaService.obtenerTodas();
        
        String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
        model.addAttribute("usuarioAdmins", rolNombre);
        model.addAttribute("producto", new ProductoCreateDTO());
        model.addAttribute("categorias", categorias);
        model.addAttribute("esEdicion", false);
        
        log.debug("Mostrando formulario de nuevo producto");
        return "administrador/productoFormulario";
    }

    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_EDITAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String editarProducto(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            ProductoResponseDTO producto = productoService.buscarPorIdConDetalles(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            List<CategoriaDTO> categorias = categoriaService.obtenerTodas();
            Usuario usuario = userDetails.getUsuario();
            
            ProductoCreateDTO productoParaEditar = ProductoCreateDTO.builder()
                    .idProducto(producto.getIdProducto())
                    .nombre(producto.getNombre())
                    .precio(producto.getPrecio())
                    .descripcion(producto.getDescripcion())
                    .stock(producto.getStock())
                    .estado(producto.getEstado())
                    .imagenUrl(producto.getImagenUrl())
                    .idCategoria(producto.getCategoria().getIdCategoria())
                    .nombreCategoria(producto.getCategoria().getNombre())
                    .build();
            
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("producto", productoParaEditar);
            model.addAttribute("categorias", categorias);
            model.addAttribute("esEdicion", true);
            
            log.debug("Cargando producto para editar: ID={}", id);
            return "administrador/productoFormulario";
            
        } catch (Exception e) {
            log.error("Error al cargar producto para editar: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/admin/productos";
        }
    }


    @PostMapping("/guardar")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_CREAR', 'PRODUCTOS_EDITAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String guardarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoriaNombre,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "disponible", defaultValue = "false") Boolean disponible,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("stock") Integer stock,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("Guardando producto. Nombre: {}, ID: {}", nombre, id);
            
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del producto es obligatorio");
            }
            
            if (categoriaNombre == null || categoriaNombre.trim().isEmpty()) {
                throw new IllegalArgumentException("Debes seleccionar o crear una categoria");
            }
            
            if (precio == null || precio <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a 0");
            }
            
            if (stock == null || stock < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo");
            }

            CategoriaDTO categoria = categoriaService.buscarPorNombre(categoriaNombre.trim())
                    .orElseGet(() -> {
                        CategoriaCreateDTO newCat = CategoriaCreateDTO.builder()
                                .nombre(categoriaNombre.trim())
                                .build();
                        return categoriaService.crear(newCat);
                    });
            
            ProductoCreateDTO productoDTO = ProductoCreateDTO.builder()
                    .idProducto(id)
                    .nombre(nombre.trim())
                    .precio(precio)
                    .descripcion(descripcion.trim())
                    .stock(stock)
                    .estado(disponible)
                    .idCategoria(categoria.getIdCategoria())
                    .nombreCategoria(categoria.getNombre())
                    .build();
            

            if (id == null) {
                productoService.crear(productoDTO, imagen);
                log.info("Producto creado exitosamente");
                redirectAttributes.addFlashAttribute("mensaje", "Producto creado correctamente");
            } else {
                productoService.actualizar(id, productoDTO, imagen);
                log.info("Producto actualizado exitosamente");
                redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado correctamente");
            }
            
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validacion al guardar producto", e);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        } catch (Exception e) {
            log.error("Error inesperado al guardar producto", e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/productos";
    }


    @PostMapping("/{id}/actualizar-stock")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_EDITAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String actualizarStock(
            @PathVariable Integer id,
            @RequestParam("stockAAgregar") Integer stockAAgregar,
            RedirectAttributes redirectAttributes) {
        try {
            ProductoDTO producto = productoService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            productoService.aumentarStock(id, stockAAgregar);
            
            log.info("Stock actualizado: ID={}, Stock agregado={}", id, stockAAgregar);
            redirectAttributes.addFlashAttribute("mensaje", "Stock actualizado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            log.error("Error al actualizar stock", e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar stock: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/productos";
    }


    @PostMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    @ResponseBody
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id) {
        try {
            ProductoDTO producto = productoService.buscarPorId(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            Boolean nuevoEstado = !producto.getEstado();
            productoService.cambiarEstado(id, nuevoEstado);
            
            log.info("Estado cambiado para producto ID={}, Nuevo estado={}", id, nuevoEstado);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "mensaje", nuevoEstado ? "Producto activado" : "Producto desactivado",
                "nuevoEstado", nuevoEstado
            ));
            
        } catch (Exception e) {
            log.error("Error al cambiar estado del producto: ID={}", id, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "mensaje", "Error: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/{id}/desactivar")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_ELIMINAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public String desactivarProducto(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarPorId(id);
            
            log.info("Producto desactivado: ID={}", id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto desactivado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            log.error("Error al desactivar producto: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al desactivar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/productos";
    }

    @PostMapping("/{id}/activar")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public String activarProducto(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoService.activarProducto(id);
            
            log.info("Producto activado: ID={}", id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto activado correctamente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            log.error("Error al activar producto: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al activar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/productos";
    }


    @PostMapping("/{id}/eliminar-permanente")
    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public String eliminarPermanente(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarPermanentemente(id);
            
            log.warn("Producto eliminado permanentemente: ID={}", id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto eliminado permanentemente");
            redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            
        } catch (Exception e) {
            log.error("Error al eliminar permanentemente producto: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/productos";
    }
}
