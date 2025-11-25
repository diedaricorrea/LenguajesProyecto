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


@Controller
public class LoginController {
    
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    
    private final UsuarioServiceImpl usuarioService;

    public LoginController(UsuarioServiceImpl usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/catalogo";
    }
    
    @GetMapping("/sobre-nosotros")
    public String sobreNosotros(Model model) {
        // Agregar objeto para el formulario de registro (modales)
        model.addAttribute("usuarioRegistro", new UsuarioRegistroDTO());
        return "usuario/sobreNosotros";
    }


    @GetMapping("/login2")
    public String redirectPorRol(Authentication authentication) {

        String rol = authentication.getAuthorities().stream()
                .map(auth -> auth.getAuthority())
                .filter(auth -> auth.startsWith("ROLE_"))
                .findFirst()
                .orElse("ROLE_USUARIO");
        
        logger.info("Usuario autenticado con rol: {}", rol);
        logger.debug("Todas las autoridades: {}", authentication.getAuthorities());
        

        if (rol.equals("ROLE_USUARIO")) {
            return "redirect:/catalogo";
        }
        

        return "redirect:/admin/productos";
    }


    @PostMapping("/register/save")
    public String registrarUsuario(
            @ModelAttribute("usuarioRegistro") @Valid UsuarioRegistroDTO registroDTO,
            BindingResult resultado,
            RedirectAttributes redirectAttributes,
            Model model) {
        
        logger.info("Intentando registrar nuevo usuario con código: {}", registroDTO.getCodigoEstudiantil());
        

        if (resultado.hasErrors()) {
            logger.warn("Errores de validación en el registro");
            redirectAttributes.addFlashAttribute("error", "Por favor verifica los datos del formulario");
            redirectAttributes.addFlashAttribute("showRegisterModal", true);
            return "redirect:/catalogo";
        }
        
        try {
 
            UsuarioResponseDTO creado = usuarioService.registrarUsuario(registroDTO);
            
            logger.info("Usuario registrado exitosamente: {}", creado.getCorreo());
            redirectAttributes.addFlashAttribute("success", 
                "¡Cuenta creada exitosamente! Ya puedes iniciar sesión con tu código: " + registroDTO.getCodigoEstudiantil());
            redirectAttributes.addFlashAttribute("showLoginModal", true);
            
            return "redirect:/catalogo";
            
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación al registrar: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("showRegisterModal", true);
            return "redirect:/catalogo";
            
        } catch (Exception e) {
            logger.error("Error inesperado al registrar usuario", e);
            redirectAttributes.addFlashAttribute("error", "Error al registrar el usuario. Intente nuevamente.");
            redirectAttributes.addFlashAttribute("showRegisterModal", true);
            return "redirect:/catalogo";
        }
    }
}
