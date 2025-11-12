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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para gestión de categorías usando DTOs
 */
@Controller
@RequestMapping("/categorias")
@RequiredArgsConstructor
@Slf4j
public class CategoriaController {

    private final CategoriaService categoriaService;

    /**
     * Muestra la página principal de gestión de categorías
     */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_VER', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String index(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        Usuario usuario = userDetails.getUsuario();
        
        List<CategoriaResponseDTO> categorias = categoriaService.findAllWithDetails();
        
        String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
        model.addAttribute("usuarioAdmins", rolNombre);
        model.addAttribute("usuarioNombre", usuario.getNombre());
        model.addAttribute("categorias", categorias);
        model.addAttribute("categoria", new CategoriaCreateDTO());
        
        log.debug("Mostrando página de categorías. Total: {}", categorias.size());
        return "administrador/categorias";
    }

    /**
     * Guarda o actualiza una categoría
     */
    @PostMapping("/guardar")
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_CREAR', 'CATEGORIAS_EDITAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String guardar(@Valid @ModelAttribute("categoria") CategoriaCreateDTO categoriaDTO,
                         BindingResult bindingResult,
                         RedirectAttributes redirectAttributes,
                         Model model,
                         @AuthenticationPrincipal UsuarioDetails userDetails) {
        
        if (bindingResult.hasErrors()) {
            log.warn("Errores de validación al guardar categoría: {}", bindingResult.getAllErrors());
            
            // Recargar datos para mostrar la vista con errores
            Usuario usuario = userDetails.getUsuario();
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("usuarioNombre", usuario.getNombre());
            model.addAttribute("categorias", categoriaService.findAllWithDetails());
            model.addAttribute("mensaje", "Por favor corrige los errores en el formulario");
            model.addAttribute("tipoMensaje", "danger");
            
            return "administrador/categorias";
        }
        
        try {
            if (categoriaDTO.getIdCategoria() == null) {
                // Crear nueva categoría
                CategoriaDTO createdCategoria = categoriaService.create(categoriaDTO);
                log.info("Categoría creada: ID={}, Nombre={}", createdCategoria.getIdCategoria(), createdCategoria.getNombre());
                redirectAttributes.addFlashAttribute("mensaje", "Categoría creada correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            } else {
                // Actualizar categoría existente
                CategoriaDTO updatedCategoria = categoriaService.update(categoriaDTO.getIdCategoria(), categoriaDTO);
                log.info("Categoría actualizada: ID={}", updatedCategoria.getIdCategoria());
                redirectAttributes.addFlashAttribute("mensaje", "Categoría actualizada correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            }
        } catch (IllegalArgumentException e) {
            log.error("Error de validación al guardar categoría", e);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        } catch (Exception e) {
            log.error("Error inesperado al guardar categoría", e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al guardar la categoría: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/categorias";
    }

    /**
     * Carga una categoría para editar
     */
    @GetMapping("/editar/{id}")
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_EDITAR', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public String editar(@PathVariable Integer id,
                        Model model,
                        @AuthenticationPrincipal UsuarioDetails userDetails,
                        RedirectAttributes redirectAttributes) {
        
        try {
            Usuario usuario = userDetails.getUsuario();
            
            CategoriaDTO categoriaDTO = categoriaService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
            
            // Convertir a CategoriaCreateDTO para el formulario
            CategoriaCreateDTO categoriaCreateDTO = CategoriaCreateDTO.builder()
                    .idCategoria(categoriaDTO.getIdCategoria())
                    .nombre(categoriaDTO.getNombre())
                    .build();
            
            List<CategoriaResponseDTO> categorias = categoriaService.findAllWithDetails();
            
            String rolNombre = usuario.getRol() != null ? usuario.getRol().toString() : "USUARIO";
            model.addAttribute("usuarioAdmins", rolNombre);
            model.addAttribute("usuarioNombre", usuario.getNombre());
            model.addAttribute("categorias", categorias);
            model.addAttribute("categoria", categoriaCreateDTO);
            model.addAttribute("modoEdicion", true);
            
            log.debug("Cargando categoría para editar: ID={}", id);
            return "administrador/categorias";
            
        } catch (Exception e) {
            log.error("Error al cargar categoría para editar: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
            return "redirect:/categorias";
        }
    }

    /**
     * Elimina una categoría
     */
    @GetMapping("/eliminar/{id}")
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_ELIMINAR', 'ROLE_ADMINISTRADOR')")
    public String eliminar(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            long cantidadProductos = categoriaService.countProductosByCategoria(id);
            
            if (cantidadProductos > 0) {
                log.warn("Intento de eliminar categoría con productos asociados. ID={}, Productos={}", id, cantidadProductos);
                redirectAttributes.addFlashAttribute("mensaje", 
                    "No se puede eliminar la categoría porque tiene " + cantidadProductos + " producto(s) asociado(s)");
                redirectAttributes.addFlashAttribute("tipoMensaje", "warning");
            } else {
                categoriaService.deleteById(id);
                log.info("Categoría eliminada: ID={}", id);
                redirectAttributes.addFlashAttribute("mensaje", "Categoría eliminada correctamente");
                redirectAttributes.addFlashAttribute("tipoMensaje", "success");
            }
        } catch (IllegalArgumentException e) {
            log.error("Categoría no encontrada para eliminar: ID={}", id);
            redirectAttributes.addFlashAttribute("mensaje", e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        } catch (Exception e) {
            log.error("Error al eliminar categoría: ID={}", id, e);
            redirectAttributes.addFlashAttribute("mensaje", "Error al eliminar la categoría: " + e.getMessage());
            redirectAttributes.addFlashAttribute("tipoMensaje", "danger");
        }
        
        return "redirect:/categorias";
    }

    /**
     * API REST para obtener todas las categorías (útil para AJAX)
     */
    @GetMapping("/api/listar")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_VER', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR', 'ROLE_USUARIO')")
    public List<CategoriaDTO> listarCategorias() {
        log.debug("API: Listando todas las categorías");
        return categoriaService.findAll();
    }

    /**
     * API REST para verificar si existe una categoría con el nombre dado
     */
    @GetMapping("/api/existe")
    @ResponseBody
    @PreAuthorize("hasAnyAuthority('CATEGORIAS_VER', 'ROLE_ADMINISTRADOR', 'ROLE_TRABAJADOR')")
    public boolean existeCategoria(@RequestParam String nombre) {
        log.debug("API: Verificando existencia de categoría: {}", nombre);
        return categoriaService.existsByNombre(nombre);
    }
}

