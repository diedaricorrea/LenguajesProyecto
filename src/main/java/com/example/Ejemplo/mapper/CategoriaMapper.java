package com.example.Ejemplo.mapper;

import com.example.Ejemplo.dto.CategoriaCreateDTO;
import com.example.Ejemplo.dto.CategoriaDTO;
import com.example.Ejemplo.dto.CategoriaResponseDTO;
import com.example.Ejemplo.models.Categoria;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Categoria (Entity) y sus DTOs
 */
@Component
public class CategoriaMapper {
    
    /**
     * Convierte una entidad Categoria a CategoriaDTO
     */
    public CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        
        return CategoriaDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .build();
    }
    
    /**
     * Convierte una entidad Categoria a CategoriaResponseDTO
     * NOTA: Usa un método seguro para contar productos sin inicializar la colección lazy
     */
    public CategoriaResponseDTO toResponseDTO(Categoria categoria) {
        if (categoria == null) {
            return null;
        }
        
        // Evitar cargar la colección lazy de productos
        // Solo intenta contar si la colección ya está inicializada
        long cantidadProductos = 0L;
        try {
            if (categoria.getProductos() != null && 
                org.hibernate.Hibernate.isInitialized(categoria.getProductos())) {
                cantidadProductos = categoria.getProductos().size();
            }
        } catch (Exception e) {
            // Si hay error al acceder a productos, dejar en 0
            cantidadProductos = 0L;
        }
        
        return CategoriaResponseDTO.builder()
                .idCategoria(categoria.getIdCategoria())
                .nombre(categoria.getNombre())
                .cantidadProductos(cantidadProductos)
                .tieneProdutos(cantidadProductos > 0)
                .build();
    }
    
    /**
     * Convierte un CategoriaCreateDTO a entidad Categoria
     */
    public Categoria toEntity(CategoriaCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(dto.getIdCategoria());
        categoria.setNombre(dto.getNombre());
        
        return categoria;
    }
    
    /**
     * Convierte un CategoriaDTO a entidad Categoria
     */
    public Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(dto.getIdCategoria());
        categoria.setNombre(dto.getNombre());
        
        return categoria;
    }
    
    /**
     * Actualiza una entidad Categoria existente con datos de un DTO
     */
    public void updateEntity(Categoria categoria, CategoriaCreateDTO dto) {
        if (categoria == null || dto == null) {
            return;
        }
        
        categoria.setNombre(dto.getNombre());
    }
}
