package com.example.Ejemplo.controllers;

import com.example.Ejemplo.dto.UsuarioRegistroDTO;
import com.example.Ejemplo.dto.UsuarioResponseDTO;
import com.example.Ejemplo.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador de autenticación y registro
 * Refactorizado para usar DTOs
 */
@Controller
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    private final UsuarioServiceImpl usuarioService;

    public LoginController(UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }

    @PostMapping("/login")
    public String login2() {
        return "login/login";
    }

    @GetMapping("/login/medio")
    public String loginMedio() {
        return "login/medio";
    }

    /**
     * Redirecciona según el rol del usuario autenticado
     */
    @GetMapping("/login2")
    public String redirectPorRol(Authentication authentication) {
        // Buscar el rol (authority que empieza con ROLE_)
        String rol = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(auth -> auth.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_USUARIO");
        
        logger.info("Usuario autenticado con rol: {}", rol);
        logger.debug("Todas las autoridades: {}", authentication.getAuthorities());
        
        // Si tiene rol USUARIO (del catálogo público), va al catálogo
        if (rol.equals("ROLE_USUARIO")) {
            return "redirect:/catalogo";
        }
        
        // Cualquier otro rol (ADMINISTRADOR, TRABAJADOR, roles personalizados) va al panel medio
        // El panel medio redirigirá según sus permisos
        return "redirect:/login/medio";
    }

    /**
     * Muestra el formulario de registro
     */
    @GetMapping("/register")
    public String registro(Model model) {
        model.addAttribute("usuarioRegistro", new UsuarioRegistroDTO());
        return "login/register";
    }

    /**
     * Procesa el registro de un nuevo usuario
     */
    @PostMapping("/register/save")
    public String registrarUsuario(
            @ModelAttribute("usuarioRegistro") @Valid UsuarioRegistroDTO registroDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        logger.info("Intentando registrar nuevo usuario: {}", registroDTO.getCorreo());
        
        // Validar errores de formulario
        if (resultado.hasErrors()) {
            logger.warn("Errores de validación en el registro");
            model.addAttribute("usuarioRegistro", registroDTO);
            return "login/register";
        }
        
        try {
            // Registrar usuario
            UsuarioResponseDTO creado = usuarioService.registrarUsuario(registroDTO);
            
            logger.info("Usuario registrado exitosamente: {}", creado.getCorreo());
            redirectAttributes.addFlashAttribute("success", 
                "Usuario registrado con éxito. Ahora puedes iniciar sesión.");
            
            return "redirect:/login";
            
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al registrar: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("usuarioRegistro", registroDTO);
            return "login/register";
            
        } catch (Exception e) {
            logger.error("Error inesperado al registrar usuario", e);
            model.addAttribute("error", "Error al registrar el usuario. Intente nuevamente.");
            model.addAttribute("usuarioRegistro", registroDTO);
            return "login/register";
        }
    }
}
