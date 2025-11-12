package com.example.Ejemplo.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para crear/actualizar Producto - con validaciones completas
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductoCreateDTO {
    
    private Integer idProducto; // Null para crear, con valor para actualizar
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;
    
    @NotNull(message = "El precio es obligatorio")
    @Positive(message = "El precio debe ser mayor a 0")
    @DecimalMin(value = "0.01", message = "El precio mínimo es 0.01")
    private Double precio;
    
    @NotBlank(message = "La descripción es obligatoria")
    @Size(min = 10, max = 500, message = "La descripción debe tener entre 10 y 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El stock es obligatorio")
    @PositiveOrZero(message = "El stock no puede ser negativo")
    private Integer stock;
    
    @NotNull(message = "Debes indicar si el producto está disponible")
    private Boolean estado;
    
    @Size(max = 255, message = "La URL de la imagen no puede exceder 255 caracteres")
    private String imagenUrl;
    
    // Referencia a la categoría (puede ser ID o nombre)
    @NotNull(message = "La categoría es obligatoria")
    private Integer idCategoria;
    
    // Alternativa: permitir crear categoría on-the-fly con nombre
    private String nombreCategoria; // Si viene esto y no existe, se crea
}
