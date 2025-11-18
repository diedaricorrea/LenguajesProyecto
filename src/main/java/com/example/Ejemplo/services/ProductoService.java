package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.ProductoCreateDTO;
import com.example.Ejemplo.dto.ProductoDTO;
import com.example.Ejemplo.dto.ProductoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


public interface ProductoService {
    
    // ============= CONSULTAS =============
    
    /**
     * Obtiene todos los productos como DTOs simples
     */
    List<ProductoDTO> obtenerTodos();
    
    /**
     * Obtiene todos los productos con detalles completos
     */
    List<ProductoResponseDTO> obtenerTodosConDetalles();
    
    /**
     * Busca un producto por ID
     */
    Optional<ProductoDTO> buscarPorId(Integer id);
    
    /**
     * Busca un producto por ID con detalles completos
     */
    Optional<ProductoResponseDTO> buscarPorIdConDetalles(Integer id);
    
    /**
     * Busca producto por nombre
     */
    Optional<ProductoDTO> buscarPorNombre(String nombre);
    
    /**
     * Obtiene productos recientes (últimos agregados)
     */
    List<ProductoDTO> obtenerRecientes(int limite);
    
    /**
     * Busca productos por nombre (case insensitive)
     */
    List<ProductoDTO> buscarPorNombreContiene(String nombre);
    
    // ============= PAGINACIÓN =============
    
    /**
     * Obtiene productos paginados
     */
    Page<ProductoDTO> obtenerTodosPaginado(Pageable pageable);
    
    /**
     * Obtiene productos por categoría paginados
     */
    Page<ProductoDTO> obtenerPorCategoriaPaginado(String nombreCategoria, Pageable pageable);
    
    /**
     * Busca productos por categoría y/o nombre con paginación
     */
    Page<ProductoDTO> buscarPorCategoriaYNombre(String categoria, String nombre, Pageable pageable);
    
    // ============= OPERACIONES CRUD =============
    
    /**
     * Crea un nuevo producto con imagen opcional
     */
    ProductoDTO crear(ProductoCreateDTO createDTO, MultipartFile imagen);
    
    /**
     * Actualiza un producto existente
     */
    ProductoDTO actualizar(Integer id, ProductoCreateDTO updateDTO, MultipartFile imagen);
    
    /**
     * Elimina un producto (soft delete - cambia estado a false)
     */
    void eliminarPorId(Integer id);
    
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
    void eliminarPermanentemente(Integer id);
    
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
    boolean estaDisponible(Integer idProducto);
    
    /**
     * Verifica si existe un producto con el nombre dado
     */
    boolean existePorNombre(String nombre);
}
