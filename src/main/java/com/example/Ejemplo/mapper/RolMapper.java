package com.example.Ejemplo.mapper;

import com.example.Ejemplo.dto.RolCreateDTO;
import com.example.Ejemplo.dto.RolDTO;
import com.example.Ejemplo.dto.RolResponseDTO;
import com.example.Ejemplo.dto.PermisoDTO;
import com.example.Ejemplo.models.RolEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre RolEntity y sus DTOs
 */
@Component
public class RolMapper {
    
    private final PermisoMapper permisoMapper;
    
    public RolMapper(PermisoMapper permisoMapper) {
        this.permisoMapper = permisoMapper;
    }
    
    /**
     * Convierte una entidad RolEntity a RolDTO
     */
    public RolDTO toDTO(RolEntity rol) {
        if (rol == null) {
            return null;
        }
        
        // Contar permisos de manera segura
        int cantidadPermisos = 0;
        if (rol.getPermisos() != null && org.hibernate.Hibernate.isInitialized(rol.getPermisos())) {
            cantidadPermisos = rol.getPermisos().size();
        }
        
        // Contar usuarios de manera segura
        int cantidadUsuarios = 0;
        if (rol.getUsuarios() != null && org.hibernate.Hibernate.isInitialized(rol.getUsuarios())) {
            cantidadUsuarios = rol.getUsuarios().size();
        }
        
        return RolDTO.builder()
                .idRol(rol.getIdRol())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.isActivo())
                .cantidadPermisos(cantidadPermisos)
                .cantidadUsuarios(cantidadUsuarios)
                .build();
    }
    
    /**
     * Convierte una entidad RolEntity a RolResponseDTO completo
     */
    public RolResponseDTO toResponseDTO(RolEntity rol) {
        if (rol == null) {
            return null;
        }
        
        // Convertir permisos a DTOs
        Set<PermisoDTO> permisosDTO = new HashSet<>();
        if (rol.getPermisos() != null && org.hibernate.Hibernate.isInitialized(rol.getPermisos())) {
            permisosDTO = rol.getPermisos().stream()
                    .map(permisoMapper::toDTO)
                    .collect(Collectors.toSet());
        }
        
        // Contar usuarios
        int cantidadUsuarios = 0;
        if (rol.getUsuarios() != null && org.hibernate.Hibernate.isInitialized(rol.getUsuarios())) {
            cantidadUsuarios = rol.getUsuarios().size();
        }
        
        // Verificar si es un rol del sistema (no eliminable)
        boolean esSistema = rol.getNombre().equals("ADMINISTRADOR") 
                         || rol.getNombre().equals("TRABAJADOR") 
                         || rol.getNombre().equals("USUARIO");
        
        return RolResponseDTO.builder()
                .idRol(rol.getIdRol())
                .nombre(rol.getNombre())
                .descripcion(rol.getDescripcion())
                .activo(rol.isActivo())
                .permisos(permisosDTO)
                .cantidadUsuarios(cantidadUsuarios)
                .esSistema(esSistema)
                .build();
    }
    
    /**
     * Convierte un RolCreateDTO a entidad RolEntity
     */
    public RolEntity toEntity(RolCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        RolEntity rol = new RolEntity();
        rol.setNombre(dto.getNombre().toUpperCase().trim());
        rol.setDescripcion(dto.getDescripcion());
        rol.setActivo(dto.getActivo() != null ? dto.getActivo() : true);
        rol.setPermisos(new HashSet<>());
        rol.setUsuarios(new HashSet<>());
        
        return rol;
    }
    
    /**
     * Actualiza una entidad RolEntity existente con datos de un DTO
     */
    public void updateEntity(RolEntity rol, RolCreateDTO dto) {
        if (rol == null || dto == null) {
            return;
        }
        
        rol.setNombre(dto.getNombre().toUpperCase().trim());
        rol.setDescripcion(dto.getDescripcion());
        if (dto.getActivo() != null) {
            rol.setActivo(dto.getActivo());
        }
    }
}
