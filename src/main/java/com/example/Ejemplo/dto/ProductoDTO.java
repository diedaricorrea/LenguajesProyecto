package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO básico para Producto - usado en listados y operaciones simples
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoDTO {
    
    private Integer idProducto;
    private String nombre;
    private Double precio;
    private String descripcion;
    private Integer stock;
    private Boolean estado;
    private String imagenUrl;
    
    // Información de la categoría (solo ID y nombre, no toda la entidad)
    private Integer idCategoria;
    private String nombreCategoria;
}
