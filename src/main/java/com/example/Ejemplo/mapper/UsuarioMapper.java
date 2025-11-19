package com.example.Ejemplo.mapper;

import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.models.Rol;
import com.example.Ejemplo.models.RolEntity;
import com.example.Ejemplo.models.Usuario;
import com.example.Ejemplo.repository.RolEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre Usuario (Entity) y sus DTOs
 * Sigue el mismo patrón que ProductoMapper y CategoriaMapper
 */
@Component
@RequiredArgsConstructor
public class UsuarioMapper {
    
    private final RolEntityRepository rolEntityRepository;
    
    /**
     * Convierte una entidad Usuario a UsuarioDTO
     */
    public UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        return UsuarioDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rolNombre(usuario.getRolNombre())
                .fechaIngreso(usuario.getFechaIngreso())
                .estado(usuario.isEstado())
                .build();
    }
    
    /**
     * Convierte una entidad Usuario a UsuarioResponseDTO completo
     */
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        
        String estadoTexto = usuario.isEstado() ? "ACTIVO" : "INACTIVO";
        
        // Obtener permisos si usa RolEntity
        Set<String> permisos = null;
        if (usuario.getRolEntity() != null && usuario.getRolEntity().getPermisos() != null) {
            permisos = usuario.getRolEntity().getPermisos().stream()
                    .map(p -> p.getNombre())
                    .collect(Collectors.toSet());
        }
        
        // Contar pedidos de manera segura
        Integer cantidadPedidos = 0;
        try {
            if (usuario.getPedidos() != null && 
                org.hibernate.Hibernate.isInitialized(usuario.getPedidos())) {
                cantidadPedidos = usuario.getPedidos().size();
            }
        } catch (Exception e) {
            cantidadPedidos = 0;
        }
        
        return UsuarioResponseDTO.builder()
                .idUsuario(usuario.getIdUsuario())
                .nombre(usuario.getNombre())
                .correo(usuario.getCorreo())
                .rolNombre(usuario.getRolNombre())
                .fechaIngreso(usuario.getFechaIngreso())
                .estado(usuario.isEstado())
                .estadoTexto(estadoTexto)
                .permisos(permisos)
                .cantidadPedidos(cantidadPedidos)
                .build();
    }
    
    /**
     * Convierte un UsuarioCreateDTO a entidad Usuario (sin encriptar password)
     * NOTA: La contraseña debe encriptarse en el servicio
     */
    public Usuario toEntity(UsuarioCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(dto.getPassword()); // Sin encriptar, se hace en el servicio
        
        // Buscar RolEntity por nombre
        RolEntity rolEntity = rolEntityRepository.findByNombre(dto.getRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + dto.getRol()));
        usuario.setRolEntity(rolEntity);
        
        // Mantener compatibilidad con enum (intentar mapear si existe)
        try {
            usuario.setRol(Rol.valueOf(dto.getRol().toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Si no es un rol enum válido, usar USUARIO como fallback
            usuario.setRol(Rol.USUARIO);
        }
        
        usuario.setEstado(true); // Por defecto activo
        
        return usuario;
    }
    
    /**
     * Convierte un UsuarioRegistroDTO a entidad Usuario
     * Usado para registro público (siempre rol USUARIO)
     */
    public Usuario toEntityFromRegistro(UsuarioRegistroDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setCorreo(dto.getCorreo());
        usuario.setPassword(dto.getPassword()); // Sin encriptar, se hace en el servicio
        usuario.setRol(Rol.USUARIO); // Forzar rol USUARIO
        usuario.setEstado(true);
        
        return usuario;
    }
    
    /**
     * Actualiza una entidad Usuario existente con datos de UsuarioUpdateDTO
     * No modifica password, correo ni fechaIngreso
     */
    public void updateEntity(Usuario usuario, UsuarioUpdateDTO dto) {
        if (usuario == null || dto == null) {
            return;
        }
        
        usuario.setNombre(dto.getNombre());
        
        // Buscar RolEntity por nombre
        RolEntity rolEntity = rolEntityRepository.findByNombre(dto.getRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + dto.getRol()));
        usuario.setRolEntity(rolEntity);
        
        // Mantener compatibilidad con enum (intentar mapear si existe)
        try {
            usuario.setRol(Rol.valueOf(dto.getRol().toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Si no es un rol enum válido, usar USUARIO como fallback
            usuario.setRol(Rol.USUARIO);
        }
        
        usuario.setEstado(dto.getEstado());
    }
}
