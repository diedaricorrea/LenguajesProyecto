package com.example.Ejemplo.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO para asignar permisos a un rol
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarPermisosDTO {
    
    @NotNull(message = "El ID del rol es obligatorio")
    private Integer idRol;
    
    @NotEmpty(message = "Debe seleccionar al menos un permiso")
    private Set<Integer> idsPermisos;
}
