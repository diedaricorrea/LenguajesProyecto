package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO base para Rol
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolDTO {
    private Integer idRol;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Integer cantidadPermisos;
    private Integer cantidadUsuarios;
}
