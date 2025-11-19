package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.services.PermisoService;
import com.example.Ejemplo.services.RolEntityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class RolController {

    private static final Logger logger = LoggerFactory.getLogger(RolController.class);
    
    private final RolEntityService rolService;
    private final PermisoService permisoService;

    /**
     * Lista todos los roles
     */
    @GetMapping
    public String listarRoles(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        logger.info("Accediendo a la lista de roles");
        
        try {
            // Obtener información del usuario actual
            String rolActual = userDetails.getUsuario().getRolNombre();
            
            List<RolResponseDTO> roles = rolService.findAllRolesConPermisos();
            Map<String, Object> estadisticas = rolService.obtenerEstadisticas();
            
            model.addAttribute("usuarioAdmins", rolActual);
            model.addAttribute("roles", roles);
            model.addAttribute("estadisticas", estadisticas);
            model.addAttribute("nuevoRol", new RolCreateDTO());
            
            logger.info("Se encontraron {} roles", roles.size());
            return "administrador/rolesLista";
            
        } catch (Exception e) {
            logger.error("Error al listar roles", e);
            model.addAttribute("error", "Error al cargar los roles");
            return "error/error";
        }
    }

    /**
     * Crear nuevo rol
     */
    @PostMapping("/save")
    public String crearRol(@Valid @ModelAttribute RolCreateDTO rolDTO,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        
        logger.info("Intentando crear rol: {}", rolDTO.getNombre());
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", 
                "Error de validación: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/roles";
        }
        
        try {
            RolResponseDTO rolCreado = rolService.crearRol(rolDTO);
            redirectAttributes.addFlashAttribute("success", 
                "Rol '" + rolCreado.getNombre() + "' creado exitosamente");
            logger.info("Rol creado: {} (ID: {})", rolCreado.getNombre(), rolCreado.getIdRol());
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error al crear rol: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            
        } catch (Exception e) {
            logger.error("Error inesperado al crear rol", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el rol");
        }
        
        return "redirect:/roles";
    }

    /**
     * Actualizar rol existente
     */
    @PostMapping("/update")
    public String actualizarRol(@RequestParam Integer idRol,
                               @Valid @ModelAttribute RolCreateDTO rolDTO,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {
        
        logger.info("Intentando actualizar rol ID: {}", idRol);
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", 
                "Error de validación: " + result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/roles";
        }
        
        try {
            RolResponseDTO actualizado = rolService.actualizarRol(idRol, rolDTO);
            redirectAttributes.addFlashAttribute("success", 
                "Rol '" + actualizado.getNombre() + "' actualizado exitosamente");
            logger.info("Rol actualizado: {}", actualizado.getNombre());
            
        } catch (IllegalArgumentException e) {
            logger.warn("Error al actualizar rol: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar rol", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el rol");
        }
        
        return "redirect:/roles";
    }

    /**
     * Cambiar estado del rol (activar/desactivar)
     */
    @PostMapping("/{id}/cambiar-estado")
    @ResponseBody
    public Map<String, Object> cambiarEstado(@PathVariable Integer id,
                                            @RequestParam boolean activo) {
        try {
            rolService.cambiarEstado(id, activo);
            String mensaje = activo ? "Rol activado" : "Rol desactivado";
            
            return Map.of(
                "success", true,
                "message", mensaje
            );
            
        } catch (IllegalArgumentException e) {
            return Map.of(
                "success", false,
                "message", e.getMessage()
            );
            
        } catch (Exception e) {
            logger.error("Error al cambiar estado del rol", e);
            return Map.of(
                "success", false,
                "message", "Error al cambiar el estado del rol"
            );
        }
    }

    /**
     * Eliminar rol
     */
    @PostMapping("/{id}/eliminar")
    @ResponseBody
    public Map<String, Object> eliminarRol(@PathVariable Integer id) {
        try {
            rolService.eliminarRol(id);
            
            return Map.of(
                "success", true,
                "message", "Rol eliminado exitosamente"
            );
            
        } catch (IllegalArgumentException e) {
            logger.warn("No se pudo eliminar el rol: {}", e.getMessage());
            return Map.of(
                "success", false,
                "message", e.getMessage()
            );
            
        } catch (Exception e) {
            logger.error("Error al eliminar rol", e);
            return Map.of(
                "success", false,
                "message", "Error al eliminar el rol"
            );
        }
    }

    /**
     * Mostrar formulario de asignación de permisos
     */
    @GetMapping("/{id}/permisos")
    public String mostrarAsignacionPermisos(@PathVariable Integer id, 
                                           Model model,
                                           @AuthenticationPrincipal UsuarioDetails userDetails) {
        logger.info("Mostrando formulario de permisos para rol ID: {}", id);
        
        try {
            // Obtener información del usuario actual
            String rolActual = userDetails.getUsuario().getRolNombre();
            
            RolResponseDTO rol = rolService.findRolById(id);
            Map<String, List<PermisoDTO>> permisosAgrupados = 
                permisoService.findPermisosAgrupadosPorModulo();
            
            // IDs de permisos ya asignados
            List<Integer> permisosAsignados = rol.getPermisos().stream()
                .map(PermisoDTO::getIdPermiso)
                .toList();
            
            model.addAttribute("usuarioAdmins", rolActual);
            model.addAttribute("rol", rol);
            model.addAttribute("permisosAgrupados", permisosAgrupados);
            model.addAttribute("permisosAsignados", permisosAsignados);
            
            return "administrador/asignarPermisos";
            
        } catch (Exception e) {
            logger.error("Error al mostrar formulario de permisos", e);
            model.addAttribute("error", "Error al cargar los permisos");
            return "error/error";
        }
    }

    /**
     * Asignar permisos a un rol
     */
    @PostMapping("/{id}/permisos/asignar")
    public String asignarPermisos(@PathVariable Integer id,
                                 @RequestParam(required = false) List<Integer> idsPermisos,
                                 RedirectAttributes redirectAttributes) {
        
        logger.info("Asignando permisos al rol ID: {}", id);
        
        try {
            AsignarPermisosDTO asignarDTO = new AsignarPermisosDTO();
            asignarDTO.setIdRol(id);
            
            // Convertir List a Set
            if (idsPermisos != null && !idsPermisos.isEmpty()) {
                asignarDTO.setIdsPermisos(new java.util.HashSet<>(idsPermisos));
            } else {
                asignarDTO.setIdsPermisos(new java.util.HashSet<>());
            }
            
            RolResponseDTO actualizado = rolService.asignarPermisos(asignarDTO);
            
            redirectAttributes.addFlashAttribute("success", 
                "Permisos asignados exitosamente al rol '" + actualizado.getNombre() + "'");
            logger.info("Permisos asignados: {}", idsPermisos != null ? idsPermisos.size() : 0);
            
        } catch (Exception e) {
            logger.error("Error al asignar permisos", e);
            redirectAttributes.addFlashAttribute("error", "Error al asignar permisos");
        }
        
        return "redirect:/roles";
    }

    /**
     * API: Obtener rol por ID (para modales)
     */
    @GetMapping("/{id}/datos")
    @ResponseBody
    public RolResponseDTO obtenerRolDatos(@PathVariable Integer id) {
        return rolService.findRolById(id);
    }

    /**
     * API: Verificar si rol tiene usuarios asignados
     */
    @GetMapping("/{id}/tiene-usuarios")
    @ResponseBody
    public Map<String, Boolean> tieneUsuarios(@PathVariable Integer id) {
        boolean tieneUsuarios = rolService.tieneUsuariosAsignados(id);
        return Map.of("tieneUsuarios", tieneUsuarios);
    }
}
