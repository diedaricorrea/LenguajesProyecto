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
    List<CategoriaDTO> obtenerTodas();
    
    /**
     * Obtiene todas las categorías con información completa (cantidad de productos)
     */
    List<CategoriaResponseDTO> obtenerTodasConDetalles();
    
    /**
     * Busca una categoría por su ID y retorna DTO
     */
    Optional<CategoriaDTO> buscarPorId(Integer id);
    
    /**
     * Busca una categoría por su ID y retorna DTO con detalles
     */
    Optional<CategoriaResponseDTO> buscarPorIdConDetalles(Integer id);
    
    /**
     * Busca una categoría por su nombre
     */
    Optional<CategoriaDTO> buscarPorNombre(String nombre);
    
    /**
     * Crea una nueva categoría desde un DTO
     */
    CategoriaDTO crear(CategoriaCreateDTO createDTO);
    
    /**
     * Actualiza una categoría existente
     */
    CategoriaDTO actualizar(Integer id, CategoriaCreateDTO updateDTO);
    
    /**
     * Elimina una categoría por su ID
     */
    void eliminarPorId(Integer id);
    
    /**
     * Verifica si existe una categoría con el nombre dado
     */
    boolean existePorNombre(String nombre);
    
    /**
     * Cuenta la cantidad de productos asociados a una categoría
     */
    long contarProductosPorCategoria(Integer idCategoria);
    
    /**
     * Verifica si una categoría puede ser eliminada (no tiene productos)
     */
    boolean puedeSerEliminada(Integer idCategoria);
}
