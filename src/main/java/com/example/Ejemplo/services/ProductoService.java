package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.ProductoCreateDTO;
import com.example.Ejemplo.dto.ProductoDTO;
import com.example.Ejemplo.dto.ProductoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de productos usando DTOs
 * NOTA: Las funciones de carrito deberían estar en CarritoService (separación de responsabilidades)
 */
public interface ProductoService {
    
    // ============= CONSULTAS =============
    
    /**
     * Obtiene todos los productos como DTOs simples
     */
    List<ProductoDTO> findAll();
    
    /**
     * Obtiene todos los productos con detalles completos
     */
    List<ProductoResponseDTO> findAllWithDetails();
    
    /**
     * Busca un producto por ID
     */
    Optional<ProductoDTO> findById(Integer id);
    
    /**
     * Busca un producto por ID con detalles completos
     */
    Optional<ProductoResponseDTO> findByIdWithDetails(Integer id);
    
    /**
     * Busca producto por nombre
     */
    Optional<ProductoDTO> findByNombre(String nombre);
    
    /**
     * Obtiene productos recientes (últimos agregados)
     */
    List<ProductoDTO> findRecent(int limit);
    
    /**
     * Busca productos por nombre (case insensitive)
     */
    List<ProductoDTO> findByNombreContaining(String nombre);
    
    // ============= PAGINACIÓN =============
    
    /**
     * Obtiene productos paginados
     */
    Page<ProductoDTO> findAllPaginado(Pageable pageable);
    
    /**
     * Obtiene productos por categoría paginados
     */
    Page<ProductoDTO> findByCategoriaPaginado(String nombreCategoria, Pageable pageable);
    
    /**
     * Busca productos por categoría y/o nombre con paginación
     */
    Page<ProductoDTO> buscarPorCategoriaYNombre(String categoria, String nombre, Pageable pageable);
    
    // ============= CRUD OPERATIONS =============
    
    /**
     * Crea un nuevo producto con imagen opcional
     */
    ProductoDTO create(ProductoCreateDTO createDTO, MultipartFile imagen);
    
    /**
     * Actualiza un producto existente
     */
    ProductoDTO update(Integer id, ProductoCreateDTO updateDTO, MultipartFile imagen);
    
    /**
     * Elimina un producto (soft delete - cambia estado a false)
     */
    void deleteById(Integer id);
    
    /**
     * Activa un producto (cambia estado a true)
     */
    void activarProducto(Integer id);
    
    /**
     * Cambia el estado de un producto
     */
    void cambiarEstado(Integer id, Boolean nuevoEstado);
    
    /**
     * Elimina permanentemente un producto
     */
    void deletePermanently(Integer id);
    
    // ============= GESTIÓN DE STOCK =============
    
    /**
     * Reduce el stock de un producto
     */
    void reducirStock(Integer idProducto, int cantidad);
    
    /**
     * Aumenta el stock de un producto
     */
    void aumentarStock(Integer idProducto, int cantidad);
    
    /**
     * Verifica si un producto tiene stock suficiente
     */
    boolean tieneStockSuficiente(Integer idProducto, int cantidadRequerida);
    
    // ============= VALIDACIONES =============
    
    /**
     * Verifica si un producto está disponible para venta
     */
    boolean isDisponible(Integer idProducto);
    
    /**
     * Verifica si existe un producto con el nombre dado
     */
    boolean existsByNombre(String nombre);
}
