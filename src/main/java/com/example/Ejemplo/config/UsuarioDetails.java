package com.example.Ejemplo.config;


import com.example.Ejemplo.models.Permiso;
import com.example.Ejemplo.models.Usuario;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
public class UsuarioDetails implements UserDetails {
    private Usuario usuario;

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        
        // Agregar el rol como autoridad (ROLE_ADMINISTRADOR, ROLE_USUARIO, etc.)
        if (usuario.getRolEntity() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRolEntity().getNombre()));
            
            // Agregar cada permiso como autoridad
            if (usuario.getRolEntity().getPermisos() != null) {
                authorities.addAll(
                    usuario.getRolEntity().getPermisos().stream()
                        .map(permiso -> new SimpleGrantedAuthority(permiso.getNombre()))
                        .collect(Collectors.toList())
                );
            }
        } else if (usuario.getRol() != null) {
            // Fallback al enum Rol para compatibilidad
            authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().toString()));
        }
        
        return authorities;
    }

    @Override
    public String getPassword() {
        return usuario.getPassword();
    }

    @Override
    public String getUsername() {
        return usuario.getCorreo();
    }
}
