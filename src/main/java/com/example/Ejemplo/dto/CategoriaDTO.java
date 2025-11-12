package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO básico para Categoría - usado en formularios y operaciones simples
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    
    private Integer idCategoria;
    private String nombre;
    
    // Constructor de conveniencia
    public CategoriaDTO(String nombre) {
        this.nombre = nombre;
    }
}
