package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para estadísticas de usuarios
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasUsuariosDTO {
    private long totalUsuarios;
    private long usuariosActivos;
    private long administradores;
    private long trabajadores;
    private long clientes;
}
