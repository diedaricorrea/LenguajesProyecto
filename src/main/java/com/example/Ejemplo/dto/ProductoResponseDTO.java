package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoResponseDTO {
    
    private Integer idProducto;
    private String nombre;
    private Double precio;
    private String descripcion;
    private Integer stock;
    private Boolean estado;
    private String imagenUrl;
    
    // Categoría completa como DTO anidado
    private CategoriaDTO categoria;
    
    // Información adicional calculada
    private Boolean disponible; // stock > 0 && estado == true
    private String estadoTexto; // "Disponible", "Sin stock", "No disponible"
}
