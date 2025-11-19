package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.services.RolEntityService;
import com.example.Ejemplo.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Controlador para la gestión de usuarios
 * Refactorizado para usar DTOs y mejorar validaciones
 */
@Controller
@RequestMapping("/usuarios")
public class UsuariosController {
    
    private static final Logger logger = LoggerFactory.getLogger(UsuariosController.class);
    
    private final UsuarioServiceImpl usuarioService;
    private final RolEntityService rolService;
    
    public UsuariosController(UsuarioServiceImpl usuarioService, RolEntityService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    /**
     * Panel principal de gestión de usuarios
     */
    @GetMapping("/panelAdmin")
    public String panelAdmin(Model model, @AuthenticationPrincipal UsuarioDetails userDetails) {
        logger.info("Accediendo al panel de administración de usuarios");
        
        try {
            // Obtener información del usuario actual
            String rolActual = userDetails.getUsuario().getRolNombre();
            
            // Obtener lista de usuarios administrativos/trabajadores
            List<UsuarioDTO> usuarios = usuarioService.findAllUsuariosAdministrativos();
            
            // Obtener estadísticas
            EstadisticasUsuariosDTO estadisticas = usuarioService.obtenerEstadisticas();
            
            // Obtener roles activos para asignación
            List<RolDTO> rolesDisponibles = rolService.findRolesActivos();
            
            model.addAttribute("usuarioAdmins", rolActual);
            model.addAttribute("usuario", new UsuarioCreateDTO());
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("estadisticas", estadisticas);
            model.addAttribute("roles", rolesDisponibles);
            
            return "administrador/usuariosAdmin";
            
        } catch (Exception e) {
            logger.error("Error al cargar panel de usuarios", e);
            model.addAttribute("error", "Error al cargar la lista de usuarios");
            return "administrador/usuariosAdmin";
        }
    }

    /**
     * Guardar nuevo usuario
     */
    @PostMapping("/save")
    public String saveUsuario(
            @ModelAttribute @Valid UsuarioCreateDTO usuarioDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        logger.info("Intentando crear nuevo usuario: {}", usuarioDTO.getCorreo());
        
        if (resultado.hasErrors()) {
            logger.warn("Errores de validación al crear usuario");
            model.addAttribute("showModal", true);
            model.addAttribute("usuarios", usuarioService.findAllUsuariosAdministrativos());
            return "administrador/usuariosAdmin";
        }
        
        try {
            UsuarioResponseDTO creado = usuarioService.crearUsuario(usuarioDTO);
            logger.info("Usuario creado exitosamente con ID: {}", creado.getIdUsuario());
            redirectAttributes.addFlashAttribute("mensaje", "Usuario creado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            
        } catch (Exception e) {
            logger.error("Error inesperado al crear usuario", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el usuario");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        
        return "redirect:/usuarios/panelAdmin";
    }

    /**
     * Actualizar usuario existente
     */
    @PostMapping("/update")
    public String updateUsuario(
            @RequestParam Integer actId,
            @RequestParam String actNombre,
            @RequestParam String actRol,
            @RequestParam String actEstado,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Actualizando usuario ID: {}", actId);
        
        try {
            // Validaciones básicas
            if (actNombre == null || actNombre.trim().isEmpty()) {
                throw new IllegalArgumentException("El nombre no puede estar vacío");
            }
            
            if (actNombre.trim().length() < 3) {
                throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres");
            }
            
            // Crear DTO de actualización
            UsuarioUpdateDTO updateDTO = UsuarioUpdateDTO.builder()
                    .idUsuario(actId)
                    .nombre(actNombre.trim())
                    .rol(actRol.toUpperCase().trim())
                    .estado(Boolean.parseBoolean(actEstado.trim()))
                    .build();
            
            UsuarioResponseDTO actualizado = usuarioService.actualizarUsuario(updateDTO);
            
            logger.info("Usuario actualizado exitosamente: {}", actualizado.getIdUsuario());
            redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al actualizar: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("tipo", "error");
            
        } catch (Exception e) {
            logger.error("Error inesperado al actualizar usuario", e);
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el usuario");
            redirectAttributes.addFlashAttribute("tipo", "error");
        }
        
        return "redirect:/usuarios/panelAdmin";
    }

    /**
     * Buscar usuarios por ID o texto
     */
    @GetMapping("/search")
    public String usuariosSearch(
            @RequestParam(required = false) String busqueda,
            Model model,
            @AuthenticationPrincipal UsuarioDetails userDetails) {
        
        logger.info("Buscando usuarios con texto: {}", busqueda);
        
        try {
            List<UsuarioDTO> usuarios;
            
            if (busqueda == null || busqueda.trim().isEmpty()) {
                model.addAttribute("errorBusqueda", "Debe ingresar un texto de búsqueda");
                usuarios = usuarioService.findAllUsuariosAdministrativos();
            } else {
                usuarios = usuarioService.buscarUsuarios(busqueda.trim());
                
                if (usuarios.isEmpty()) {
                    model.addAttribute("mensaje", "No se encontraron usuarios con ese criterio");
                }
            }
            
            String rolActual = userDetails.getUsuario().getRolNombre();
            EstadisticasUsuariosDTO estadisticas = usuarioService.obtenerEstadisticas();
            
            model.addAttribute("usuarioAdmins", rolActual);
            model.addAttribute("usuario", new UsuarioCreateDTO());
            model.addAttribute("usuarios", usuarios);
            model.addAttribute("estadisticas", estadisticas);
            
            return "administrador/usuariosAdmin";
            
        } catch (Exception e) {
            logger.error("Error al buscar usuarios", e);
            model.addAttribute("error", "Error al realizar la búsqueda");
            return "redirect:/usuarios/panelAdmin";
        }
    }
    
    /**
     * Cambiar estado de un usuario (AJAX)
     */
    @PostMapping("/{id}/cambiar-estado")
    @ResponseBody
    public String cambiarEstado(@PathVariable Integer id, @RequestParam boolean estado) {
        logger.info("Cambiando estado del usuario {} a {}", id, estado);
        
        try {
            usuarioService.cambiarEstado(id, estado);
            return "OK";
        } catch (Exception e) {
            logger.error("Error al cambiar estado", e);
            return "ERROR: " + e.getMessage();
        }
    }
    
    /**
     * Obtener estadísticas de usuarios (API REST)
     */
    @GetMapping("/api/estadisticas")
    @ResponseBody
    public EstadisticasUsuariosDTO obtenerEstadisticas() {
        logger.info("Obteniendo estadísticas de usuarios");
        return usuarioService.obtenerEstadisticas();
    }
}
