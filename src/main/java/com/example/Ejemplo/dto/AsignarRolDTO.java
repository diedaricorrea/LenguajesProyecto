package com.example.Ejemplo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para asignar un rol a un usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AsignarRolDTO {
    
    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer idUsuario;
    
    @NotNull(message = "El ID del rol es obligatorio")
    private Integer idRol;
}
