package com.example.Ejemplo.mapper;

import com.example.Ejemplo.dto.PermisoDTO;
import com.example.Ejemplo.models.Permiso;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Permiso y PermisoDTO
 */
@Component
public class PermisoMapper {
    
    /**
     * Convierte una entidad Permiso a PermisoDTO
     */
    public PermisoDTO toDTO(Permiso permiso) {
        if (permiso == null) {
            return null;
        }
        
        return PermisoDTO.builder()
                .idPermiso(permiso.getIdPermiso())
                .nombre(permiso.getNombre())
                .descripcion(permiso.getDescripcion())
                .modulo(permiso.getModulo())
                .build();
    }
    
    /**
     * Convierte un PermisoDTO a entidad Permiso
     */
    public Permiso toEntity(PermisoDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Permiso permiso = new Permiso();
        permiso.setIdPermiso(dto.getIdPermiso());
        permiso.setNombre(dto.getNombre());
        permiso.setDescripcion(dto.getDescripcion());
        permiso.setModulo(dto.getModulo());
        
        return permiso;
    }
}
