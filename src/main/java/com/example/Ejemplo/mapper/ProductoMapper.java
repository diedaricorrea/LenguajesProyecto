package com.example.Ejemplo.mapper;

import com.example.Ejemplo.dto.ProductoCreateDTO;
import com.example.Ejemplo.dto.ProductoDTO;
import com.example.Ejemplo.dto.ProductoResponseDTO;
import com.example.Ejemplo.models.Categoria;
import com.example.Ejemplo.models.Producto;
import org.springframework.stereotype.Component;

/**
 * Mapper para convertir entre Producto (Entity) y sus DTOs
 */
@Component
public class ProductoMapper {
    
    private final CategoriaMapper categoriaMapper;
    
    public ProductoMapper(CategoriaMapper categoriaMapper) {
        this.categoriaMapper = categoriaMapper;
    }
    
    /**
     * Convierte una entidad Producto a ProductoDTO
     */
    public ProductoDTO toDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        
        return ProductoDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .descripcion(producto.getDescripcion())
                .stock(producto.getStock())
                .estado(producto.getEstado())
                .imagenUrl(producto.getImagenUrl())
                .idCategoria(producto.getCategoria() != null ? producto.getCategoria().getIdCategoria() : null)
                .nombreCategoria(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .build();
    }
    
    /**
     * Convierte una entidad Producto a ProductoResponseDTO
     */
    public ProductoResponseDTO toResponseDTO(Producto producto) {
        if (producto == null) {
            return null;
        }
        
        boolean disponible = producto.getStock() > 0 && Boolean.TRUE.equals(producto.getEstado());
        String estadoTexto;
        
        if (!Boolean.TRUE.equals(producto.getEstado())) {
            estadoTexto = "No disponible";
        } else if (producto.getStock() <= 0) {
            estadoTexto = "Sin stock";
        } else {
            estadoTexto = "Disponible";
        }
        
        return ProductoResponseDTO.builder()
                .idProducto(producto.getIdProducto())
                .nombre(producto.getNombre())
                .precio(producto.getPrecio())
                .descripcion(producto.getDescripcion())
                .stock(producto.getStock())
                .estado(producto.getEstado())
                .imagenUrl(producto.getImagenUrl())
                .categoria(categoriaMapper.toDTO(producto.getCategoria()))
                .disponible(disponible)
                .estadoTexto(estadoTexto)
                .build();
    }
    
    /**
     * Convierte un ProductoCreateDTO a entidad Producto (sin categoría)
     */
    public Producto toEntity(ProductoCreateDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Producto producto = new Producto();
        producto.setIdProducto(dto.getIdProducto());
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setStock(dto.getStock());
        producto.setEstado(dto.getEstado());
        producto.setImagenUrl(dto.getImagenUrl());
        
        // La categoría se debe setear aparte en el servicio
        return producto;
    }
    
    /**
     * Actualiza una entidad Producto existente con datos de un DTO (sin categoría)
     */
    public void updateEntity(Producto producto, ProductoCreateDTO dto) {
        if (producto == null || dto == null) {
            return;
        }
        
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setDescripcion(dto.getDescripcion());
        producto.setStock(dto.getStock());
        producto.setEstado(dto.getEstado());
        
        if (dto.getImagenUrl() != null && !dto.getImagenUrl().isEmpty()) {
            producto.setImagenUrl(dto.getImagenUrl());
        }
        
        // La categoría se debe actualizar aparte en el servicio si cambió
    }
    
    /**
     * Crea un Producto con todos los datos incluyendo la categoría
     */
    public Producto toEntityWithCategoria(ProductoCreateDTO dto, Categoria categoria) {
        Producto producto = toEntity(dto);
        if (producto != null) {
            producto.setCategoria(categoria);
        }
        return producto;
    }
}
