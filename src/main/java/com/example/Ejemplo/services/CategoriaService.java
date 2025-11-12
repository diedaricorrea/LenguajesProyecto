package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.CategoriaCreateDTO;
import com.example.Ejemplo.dto.CategoriaDTO;
import com.example.Ejemplo.dto.CategoriaResponseDTO;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de categorías usando DTOs
 */
public interface CategoriaService {
    
    /**
     * Obtiene todas las categorías como DTOs simples
     */
    List<CategoriaDTO> findAll();
    
    /**
     * Obtiene todas las categorías con información completa (cantidad de productos)
     */
    List<CategoriaResponseDTO> findAllWithDetails();
    
    /**
     * Busca una categoría por su ID y retorna DTO
     */
    Optional<CategoriaDTO> findById(Integer id);
    
    /**
     * Busca una categoría por su ID y retorna DTO con detalles
     */
    Optional<CategoriaResponseDTO> findByIdWithDetails(Integer id);
    
    /**
     * Busca una categoría por su nombre
     */
    Optional<CategoriaDTO> findByNombre(String nombre);
    
    /**
     * Crea una nueva categoría desde un DTO
     */
    CategoriaDTO create(CategoriaCreateDTO createDTO);
    
    /**
     * Actualiza una categoría existente
     */
    CategoriaDTO update(Integer id, CategoriaCreateDTO updateDTO);
    
    /**
     * Elimina una categoría por su ID
     */
    void deleteById(Integer id);
    
    /**
     * Verifica si existe una categoría con el nombre dado
     */
    boolean existsByNombre(String nombre);
    
    /**
     * Cuenta la cantidad de productos asociados a una categoría
     */
    long countProductosByCategoria(Integer idCategoria);
    
    /**
     * Verifica si una categoría puede ser eliminada (no tiene productos)
     */
    boolean canBeDeleted(Integer idCategoria);
}
