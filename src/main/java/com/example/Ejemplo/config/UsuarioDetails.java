package com.example.Ejemplo.config;

import com.example.Ejemplo.models.Usuario;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@NoArgsConstructor
@Slf4j
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
        
        
        if (usuario.getRolEntity() != null) {
            String roleName = "ROLE_" + usuario.getRolEntity().getNombre();
            authorities.add(new SimpleGrantedAuthority(roleName));
            log.debug("Usuario '{}' tiene rol: {}", usuario.getCorreo(), roleName);
            
            
            if (usuario.getRolEntity().getPermisos() != null) {
                List<GrantedAuthority> permisos = usuario.getRolEntity().getPermisos().stream()
                        .map(permiso -> new SimpleGrantedAuthority(permiso.getNombre()))
                        .collect(Collectors.toList());
                
                authorities.addAll(permisos);
                log.info("Usuario '{}' cargado con {} permisos: {}", 
                        usuario.getCorreo(), 
                        permisos.size(),
                        permisos.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", ")));
            } else {
                log.warn("Usuario '{}' con rol '{}' pero SIN permisos asignados", 
                        usuario.getCorreo(), usuario.getRolEntity().getNombre());
            }
        } else if (usuario.getRol() != null) {
           
            String roleName = "ROLE_" + usuario.getRol().toString();
            authorities.add(new SimpleGrantedAuthority(roleName));
            log.warn("Usuario '{}' usando rol ENUM (legacy): {}", usuario.getCorreo(), roleName);
        } else {
            log.error("Usuario '{}' SIN ROL asignado!", usuario.getCorreo());
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
    
    @Override
    public boolean isAccountNonExpired() {
        return true; 
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true; 
    }
    
    @Override
    public boolean isEnabled() {
       
        boolean activo = usuario.isEstado();
        if (!activo) {
            log.warn("Usuario '{}' está INACTIVO - Login denegado", usuario.getCorreo());
        }
        return activo;
    }
}
