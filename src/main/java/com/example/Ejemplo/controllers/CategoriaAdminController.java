package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.CategoriaCreateDTO;
import com.example.Ejemplo.dto.CategoriaDTO;
import com.example.Ejemplo.dto.CategoriaResponseDTO;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

/**
 * Controlador para la gestion administrativa de categorias
 */
@Controller
@RequestMapping("/admin/categorias")
@RequiredArgsConstructor
@Slf4j
public class CategoriaAdminController {

    private final CategoriaService categoriaService;

    /**
     * Lista todas las categorias con opciones de gestion
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_VER', 'CATEGORIAS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String listarCategorias(
            @RequestParam(required = false) String busqueda,
            Model model, 
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        try {
            Usuario usuario = userDetails.getUsuario();
            log.info("Usuario {} accediendo a gestion de categorias", usuario.getCorreo());

            List<CategoriaResponseDTO> categorias = categoriaService.findAllWithDetails();
            
            // Filtrar por busqueda si se proporciona
            if (busqueda != null && !busqueda.trim().isEmpty()) {
                categorias = categorias.stream()
                    .filter(c -> c.getNombre().toLowerCase().contains(busqueda.toLowerCase()))
                    .toList();
            }
            
            log.debug("Listando categorias. Total: {}", categorias.size());

            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("categorias", categorias);
            model.addAttribute("busqueda", busqueda);
            
            return "administrador/categoriasLista";
        } catch (Exception e) {
            log.error("Error al listar categorias: ", e);
            throw e;
        }
    }

    /**
     * Muestra el formulario para crear una nueva categoria
     */
    @GetMapping("/nueva")
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_CREAR', 'CATEGORIAS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String nuevaCategoria(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        
        String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
        model.addAttribute("usuarioAdmins", rolNombre);
        model.addAttribute("categoria", new CategoriaCreateDTO());
        model.addAttribute("esEdicion", false);
        
        log.debug("Mostrando formulario de nueva categoria");
        return "administrador/categoriaFormulario";
    }

    /**
     * Muestra el formulario para editar una categoria existente
     */
    @GetMapping("/{id}/editar")
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_EDITAR', 'CATEGORIAS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String editarCategoria(
            @PathVariable Integer id,
            Model model,
            @AuthenticationPrincipal UsuarioDetails userDetails,
            RedirectAttributes redirectAttributes) {
        try {
            CategoriaDTO categoria = categoriaService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Categoria no encontrada"));
            
            Usuario usuario = userDetails.getUsuario();
            
            // Convertir a CreateDTO para el formulario
            CategoriaCreateDTO categoriaParaEditar = CategoriaCreateDTO.builder()
                    .idCategoria(categoria.getIdCategoria())
                    .nombre(categoria.getNombre())
                    .build();
            
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("categoria", categoriaParaEditar);
            model.addAttribute("esEdicion", true);
            
            log.debug("Cargando categoria para editar: ID={}", id);
            return "administrador/categoriaFormulario";
            
        } catch (Exception e) {
            log.error("Error al cargar categoria para editar: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/admin/categorias";
        }
    }

    /**
     * Guarda una categoria nueva o actualiza una existente
     */
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_CREAR', 'CATEGORIAS_EDITAR', 'CATEGORIAS_GESTIONAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String guardarCategoria(
            @Valid @ModelAttribute("categoria") CategoriaCreateDTO categoriaDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        
        if (bindingResult.hasErrors()) {
            log.warn("Errores de validacion al guardar categoria: {}", bindingResult.getAllErrors());
            
            Usuario usuario = userDetails.getUsuario();
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("esEdicion", categoriaDTO.getIdCategoria() != null);
            model.addAttribute("mensaje", "Por favor corrige los errores en el formulario");
            model.addAttribute("tipoMensaje", "danger");
            
            return "administrador/categoriaFormulario";
        }
        
        try {
            if (categoriaDTO.getIdCategoria() == null) {
                // Crear nueva categoria
                CategoriaDTO createdCategoria = categoriaService.create(categoriaDTO);
                log.info("Categoria creada: ID={}, Nombre={}", createdCategoria.getIdCategoria(), createdCategoria.getNombre());
                redirectAttributes.addFlashAttribute("mensaje", "Categoria creada correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                // Actualizar categoria existente
                CategoriaDTO updatedCategoria = categoriaService.update(categoriaDTO.getIdCategoria(), categoriaDTO);
                log.info("Categoria actualizada: ID={}", updatedCategoria.getIdCategoria());
                redirectAttributes.addFlashAttribute("mensaje", "Categoria actualizada correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            }
        } catch (IllegalArgumentException e) {
            log.error("Error de validacion al guardar categoria", e);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        } catch (Exception e) {
            log.error("Error inesperado al guardar categoria", e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar la categoria: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/categorias";
    }

    /**
     * Elimina una categoria (solo si no tiene productos asociados)
     */
    @PostMapping("/{id}/eliminar")
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_ELIMINAR', 'CATEGORIAS_GESTIONAR', 'ROLE_ADMINISTRADOR')")
    public String eliminarCategoria(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            long cantidadProductos = categoriaService.countProductosByCategoria(id);
            
            if (cantidadProductos > 0) {
                log.warn("Intento de eliminar categoria con productos asociados. ID={}, Productos={}", id, cantidadProductos);
                redirectAttributes.addFlashAttribute("mensaje", 
                    "No se puede eliminar la categoria porque tiene " + cantidadProductos + " producto(s) asociado(s)");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            } else {
                categoriaService.deleteById(id);
                log.info("Categoria eliminada: ID={}", id);
                redirectAttributes.addFlashAttribute("mensaje", "Categoria eliminada correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            }
        } catch (IllegalArgumentException e) {
            log.error("Categoria no encontrada para eliminar: ID={}", id);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        } catch (Exception e) {
            log.error("Error al eliminar categoria: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la categoria: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/admin/categorias";
    }

    /**
     * API REST para obtener todas las categorias (util para AJAX)
     */
    @GetMapping("/api/listar")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_VER', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR', 'ROLE_USUARIO')")
    public ResponseEntity<List<CategoriaDTO>> listarCategoriasAPI() {
        log.debug("API: Listando todas las categorias");
        return ResponseEntity.ok(categoriaService.findAll());
    }

    /**
     * API REST para verificar si existe una categoria con el nombre dado
     */
    @GetMapping("/api/existe")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_VER', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public ResponseEntity<Map<String, Boolean>> existeCategoria(@RequestParam String nombre) {
        log.debug("API: Verificando existencia de categoria: {}", nombre);
        boolean existe = categoriaService.existsByNombre(nombre);
        return ResponseEntity.ok(Map.of("existe", existe));
    }
}

