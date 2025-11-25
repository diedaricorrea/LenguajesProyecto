package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaResponseDTO {
    
    private Integer idCategoria;
    private String nombre;
    private Long cantidadProductos;
    private Boolean tieneProdutos; 
    
    // Constructor sin cantidad de productos
    public CategoriaResponseDTO(Integer idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.cantidadProductos = 0L;
        this.tieneProdutos = false;
    }
}
