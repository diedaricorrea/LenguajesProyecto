package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.CategoriaService;
import com.example.Ejemplo.services.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para gestión de productos (Administración)
 * REFACTORIZADO: Usa DTOs en lugar de Entities
 */
@Controller
@RequestMapping("/productos")
@RequiredArgsConstructor
@Slf4j
public class AdminController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    /**
     * Panel principal de administración de productos
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_GESTIONAR', 'PRODUCTOS_VER', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String panelAdmin(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        try {
            Usuario usuario = userDetails.getUsuario();
            log.info("Usuario accediendo a /productos: {}, Rol: {}", usuario.getCorreo(), usuario.getRol());

            List<ProductoResponseDTO> productos = productoService.findAllWithDetails();
            List<CategoriaDTO> categorias = categoriaService.findAll();
            
            log.debug("Panel de productos. Total productos: {}, Total categorías: {}", 
                    productos.size(), categorias.size());

            // Obtener rol de forma segura
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("productos", productos);
            model.addAttribute("producto", new ProductoCreateDTO());
            model.addAttribute("categorias", categorias);
            
            return "administrador/productos";
        } catch (Exception e) {
            log.error("Error en panelAdmin: ", e);
            throw e;
        }
    }

    /**
     * Panel de edición de productos
     */
    @GetMapping("/edicion")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_GESTIONAR', 'PRODUCTOS_EDITAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String edicion(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        List<ProductoResponseDTO> productos = productoService.findAllWithDetails();
        
        String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
        model.addAttribute("usuarioAdmins", rolNombre);
        model.addAttribute("productos", productos);
        
        log.debug("Panel de edición. Total productos: {}", productos.size());
        return "administrador/accionesProducto";
    }

    /**
     * Búsqueda de productos por nombre
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_VER', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String buscar(Model model, @RequestParam("nombre") String nombre) {
        List<ProductoDTO> productos = productoService.findByNombreContaining(nombre);
        model.addAttribute("productos", productos);
        
        log.debug("Búsqueda de productos con nombre: '{}'. Resultados: {}", nombre, productos.size());
        return "administrador/accionesProducto";
    }

    /**
     * Actualizar stock de un producto
     */
    @PostMapping("/actualizar")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_EDITAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String actualizar(RedirectAttributes redirectAttribute,
                             @RequestParam("idProducto") Integer idProducto,
                             @RequestParam("nombre") String nombre,
                             @RequestParam("precio") Double precio,
                             @RequestParam("descripcion") String descripcion,
                             @RequestParam("stock") Integer stockAAgregar) {
        try {
            ProductoDTO producto = productoService.findById(idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            // Crear DTO de actualización
            ProductoCreateDTO updateDTO = ProductoCreateDTO.builder()
                    .idProducto(idProducto)
                    .nombre(nombre)
                    .precio(precio)
                    .descripcion(descripcion)
                    .stock(producto.getStock() + stockAAgregar) // Agregar al stock existente
                    .estado(producto.getEstado())
                    .imagenUrl(producto.getImagenUrl())
                    .idCategoria(producto.getIdCategoria())
                    .build();
            
            productoService.update(idProducto, updateDTO, null);
            
            log.info("Producto actualizado: ID={}, Stock agregado={}", idProducto, stockAAgregar);
            redirectAttribute.addFlashAttribute("mensaje", "El producto se actualizó correctamente");
            redirectAttribute.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            log.error("Error al actualizar producto", e);
            redirectAttribute.addFlashAttribute("mensaje", "Error al actualizar: " + e.getMessage());
            redirectAttribute.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/productos/edicion";
    }

    /**
     * Guardar o actualizar producto completo
     */
    @PostMapping("/subirproductos")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_CREAR', 'PRODUCTOS_EDITAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String guardarProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("categoria") String categoriaNombre,
            @RequestParam("imagen") MultipartFile imagen,
            @RequestParam(value = "disponible", defaultValue = "false") Boolean disponible,
            @RequestParam(value = "id", required = false) Integer id,
            @RequestParam("stock") Integer stock,
            RedirectAttributes redirectAttributes) {
        
        try {
            log.info("Guardando producto. Nombre: {}, ID: {}", nombre, id);
            
            // Validaciones básicas
            if (nombre == null || nombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre del producto es obligatorio");
            }
            
            if (categoriaNombre == null || categoriaNombre.trim().isEmpty()) {
                throw new IllegalArgumentException("Debes seleccionar o crear una categoría");
            }
            
            if (precio == null || precio <= 0) {
                throw new IllegalArgumentException("El precio debe ser mayor a 0");
            }
            
            if (stock == null || stock < 0) {
                throw new IllegalArgumentException("El stock no puede ser negativo");
            }
            
            // Obtener o crear categoría
            CategoriaDTO categoria = categoriaService.findByNombre(categoriaNombre.trim())
                    .orElseGet(() -> {
                        CategoriaCreateDTO newCat = CategoriaCreateDTO.builder()
                                .nombre(categoriaNombre.trim())
                                .build();
                        return categoriaService.create(newCat);
                    });
            
            // Crear DTO del producto
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
            
            // Guardar o actualizar
            if (id == null) {
                productoService.create(productoDTO, imagen);
                log.info("Producto creado exitosamente");
                redirectAttributes.addFlashAttribute("mensaje", "Producto guardado correctamente");
            } else {
                productoService.update(id, productoDTO, imagen);
                log.info("Producto actualizado exitosamente");
                redirectAttributes.addFlashAttribute("mensaje", "Producto actualizado correctamente");
            }
            
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (IllegalArgumentException e) {
            log.warn("Error de validación al guardar producto", e);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        } catch (Exception e) {
            log.error("Error inesperado al guardar producto", e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar el producto: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/productos";
    }

    /**
     * Cargar producto para edición
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_VER', 'PRODUCTOS_EDITAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String editarProducto(@PathVariable Integer id,
                                 Model model,
                                 @AuthenticationPrincipal UsuarioDetails userDetails,
                                 RedirectAttributes redirectAttributes) {
        try {
            ProductoResponseDTO producto = productoService.findByIdWithDetails(id)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            List<ProductoResponseDTO> productosRecientes = productoService.findAllWithDetails()
                    .stream()
                    .limit(5)
                    .toList();
            
            List<CategoriaDTO> categorias = categoriaService.findAll();
            Usuario usuario = userDetails.getUsuario();
            
            // Convertir a CreateDTO para el formulario
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
            model.addAttribute("productos", productosRecientes);
            model.addAttribute("categorias", categorias);
            
            log.debug("Cargando producto para editar: ID={}", id);
            return "administrador/productos";
            
        } catch (Exception e) {
            log.error("Error al cargar producto para editar: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/productos";
        }
    }

    /**
     * Eliminar (desactivar) producto
     */
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyAuthority('PRODUCTOS_ELIMINAR', 'PRODUCTOS_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoService.deleteById(id);
            
            log.info("Producto desactivado: ID={}", id);
            redirectAttributes.addFlashAttribute("mensaje", "Producto marcado como no disponible");
            redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            
        } catch (Exception e) {
            log.error("Error al eliminar producto: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/productos";
    }
}
