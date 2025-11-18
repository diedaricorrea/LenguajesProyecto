package com.example.Ejemplo.controllers;

import com.example.Ejemplo.config.UsuarioDetails;
import com.example.Ejemplo.models.Rol;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.services.impl.NotificacionServiceImpl;
import com.example.Ejemplo.services.impl.UsuarioServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController {

    private final UsuarioServiceImpl usuarioServiceImpl;
    private final NotificacionServiceImpl notificacionServiceImpl;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    public UsuariosController(UsuarioServiceImpl usuarioServiceImpl, NotificacionServiceImpl notificacionServiceImpl) {
        this.usuarioServiceImpl = usuarioServiceImpl;
        this.notificacionServiceImpl = notificacionServiceImpl;
    }

    @PostMapping("/save")
    public String saveUsuario(@ModelAttribute @Valid Usuario usuario,BindingResult resultado, RedirectAttributes redirectAttributes,Model model) {
        if(resultado.hasErrors()) {
            model.addAttribute("showModal", true); // 🔸 Esto activa el modal
            model.addAttribute("usuarios",usuarioServiceImpl.findAllUsuariosByNotRol(Rol.USUARIO));
            return "redirect:/usuarios/panelAdmin";
        }
        usuario.setEstado(true);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuarioServiceImpl.saveUser(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "Usuario guardado correctamente");
        return "redirect:/usuarios/panelAdmin";
    }

    @GetMapping("/panelAdmin")
    public String panelAdmin(Model model, @AuthenticationPrincipal UsuarioDetails userDetails){
        Usuario usuario = userDetails.getUsuario();

        model.addAttribute("usuarioAdmins", usuario.getRol().toString());
        model.addAttribute("usuario",new Usuario());
        model.addAttribute("usuarios",usuarioServiceImpl.findAllUsuariosByNotRol(Rol.USUARIO));
        return "administrador/usuariosAdmin";
    }

    @PostMapping("/update")
    public String updateUsuario(
            @RequestParam Integer actId,
            @RequestParam String actNombre,
            @RequestParam String actRol,
            @RequestParam String actEstado,
            RedirectAttributes redirectAttributes,
            Model model) {
        try{
            Usuario user = usuarioServiceImpl.findUsuarioById(actId).orElse(null);
            user.setNombre(actNombre.trim());
            user.setRol(Rol.valueOf(actRol.toUpperCase().trim()));
            actEstado.trim().toLowerCase();
            user.setEstado(Boolean.parseBoolean(actEstado));
            usuarioServiceImpl.saveUser(user);
        }catch (NullPointerException e){
            throw new RuntimeException(e);
        }

        redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado correctamente");
        return "redirect:/usuarios/panelAdmin";
    }

    @GetMapping("/search")
    public String usuariosSearch(@RequestParam(required = false) Integer id, Model model) {
        if (id == null) {
            model.addAttribute("errorId", "El id no puede ser vacio");
            model.addAttribute("usuario", new Usuario());
            model.addAttribute("usuarios",usuarioServiceImpl.findAllUsuariosByNotRol(Rol.USUARIO));
            return "administrador/usuariosAdmin";
        }
        Usuario usuario = usuarioServiceImpl.findUsuarioById(id).orElse(null);
        model.addAttribute("usuario", new Usuario());
        model.addAttribute("usuarios",usuario);

        return "administrador/usuariosAdmin";
    }
}
