package com.example.Ejemplo.config;

import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UsuarioDetailsService implements UserDetailsService{

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Intentando autenticar usuario: {}", username);
        
        Usuario usuario = null;
        
        usuario = usuarioRepository.findByCorreo(username).orElse(null);
        

        if (usuario == null) {
            log.debug("No encontrado por correo, intentando por código estudiantil...");
            usuario = usuarioRepository.findByCodigoEstudiantil(username).orElse(null);
        }
        
        if (usuario == null) {
            log.warn("❌ Usuario no encontrado: {}", username);
            throw new UsernameNotFoundException("Usuario no encontrado: " + username);
        }
        
        log.info("✅ Usuario encontrado: {} - Rol: {}", usuario.getCorreo(), usuario.getRolNombre());
        return new UsuarioDetails(usuario);
    }
}
