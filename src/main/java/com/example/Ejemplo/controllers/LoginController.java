package com.example.Ejemplo.controllers;

import com.example.Ejemplo.services.impl.UsuarioServiceImpl;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.models.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class LoginController {

    private final UsuarioServiceImpl usuarioServiceImpl;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public LoginController(UsuarioServiceImpl usuarioServiceImpl) {
        this.usuarioServiceImpl = usuarioServiceImpl;
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
    public String login3() {
        return "login/medio";
    }

    @GetMapping("/login2")
    public String login3(Authentication authentication) {
        String rol = authentication.getAuthorities().iterator().next().getAuthority();
        return switch (rol){
            case "ROLE_ADMINISTRADOR" -> "redirect:/login/medio";
            case "ROLE_TRABAJADOR" -> "redirect:/login/medio";
            case "ROLE_USUARIO" -> "redirect:/catalogo";
            default -> "login/login";
        };
    }


    @GetMapping("/register")
    public String registro() {
        return "login/register";
    }

    @PostMapping("/register/save")
    public String registrarUsuario(@RequestParam("nombre") String nombre,
                                   @RequestParam("correo") String correo,
                                   @RequestParam("password") String password, RedirectAttributes redirectAttributes) {

        // Validar si el correo ya existe
        if (usuarioServiceImpl.findUsuarioByCorreo(correo).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Ya existe una cuenta con ese correo.");
            return "redirect:/register";
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setRol(Rol.USUARIO); // 👈 Rol fijo

        Usuario u = usuarioServiceImpl.saveUser(nuevo);
        System.out.println(u.getNombre());
        redirectAttributes.addFlashAttribute("success", "Usuario registrado con éxito. Ahora puedes iniciar sesión.");
        return "redirect:/login";
    }


}
