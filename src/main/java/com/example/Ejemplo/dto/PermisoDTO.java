package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para Permiso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermisoDTO {
    private Integer idPermiso;
    private String nombre;
    private String descripcion;
    private String modulo;
}
