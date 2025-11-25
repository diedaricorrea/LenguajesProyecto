package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {
    private Integer idUsuario;
    private String nombre;
    private String correo;
    private String rolNombre;
    private LocalDateTime fechaIngreso;
    private Boolean estado;
    private String estadoTexto; // "ACTIVO" o "INACTIVO"
    private Set<String> permisos; // Lista de permisos si usa RolEntity
    private Integer cantidadPedidos;
}
