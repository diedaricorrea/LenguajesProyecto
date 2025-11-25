package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RolResponseDTO {
    private Integer idRol;
    private String nombre;
    private String descripcion;
    private Boolean activo;
    private Set<PermisoDTO> permisos;
    private Integer cantidadUsuarios;
    private Boolean esSistema; // true si es ADMINISTRADOR, TRABAJADOR, USUARIO (no eliminable)
}
