package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO base para Usuario
 * Usado para mostrar información básica de usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rolNombre;
    private LocalDateTime fechaIngreso;
    private Boolean estado;
}
