package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.models.Categoria;
import com.example.Ejemplo.models.Producto;
import com.example.Ejemplo.repository.CategoriaRepository;
import com.example.Ejemplo.repository.ProductoRepository;
import com.example.Ejemplo.services.ProductoService;
import com.example.Ejemplo.services.ImgBBUploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Productos usando DTOs
 * REFACTORIZADO: Separación de responsabilidades y uso de DTOs
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final ImgBBUploader imgBBUploader;

    // ============= CONSULTAS =============

    @Override
    public List<ProductoDTO> findAll() {
        log.debug("Obteniendo todos los productos");
        return productoRepository.findAll().stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoResponseDTO> findAllWithDetails() {
        log.debug("Obteniendo todos los productos con detalles");
        return productoRepository.findAll().stream()
                .map(productoMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ProductoDTO> findById(Integer id) {
        log.debug("Buscando producto por ID: {}", id);
        return productoRepository.findById(id)
                .map(productoMapper::toDTO);
    }

    @Override
    public Optional<ProductoResponseDTO> findByIdWithDetails(Integer id) {
        log.debug("Buscando producto con detalles por ID: {}", id);
        return productoRepository.findById(id)
                .map(productoMapper::toResponseDTO);
    }

    @Override
    public Optional<ProductoDTO> findByNombre(String nombre) {
        log.debug("Buscando producto por nombre: {}", nombre);
        List<Producto> productos = productoRepository.findByNombreContaining(nombre);
        return productos.isEmpty() ? Optional.empty() : Optional.of(productoMapper.toDTO(productos.get(0)));
    }

    @Override
    public List<ProductoDTO> findRecent(int limit) {
        log.debug("Obteniendo los {} productos más recientes", limit);
        Pageable pageable = PageRequest.of(0, limit);
        return productoRepository.findAll(pageable).stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductoDTO> findByNombreContaining(String nombre) {
        log.debug("Buscando productos que contengan: {}", nombre);
        return productoRepository.findByNombreContainingIgnoreCase(nombre.toLowerCase()).stream()
                .map(productoMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ============= PAGINACIÓN =============

    @Override
    public Page<ProductoDTO> findAllPaginado(Pageable pageable) {
        log.debug("Obteniendo productos paginados: página {}", pageable.getPageNumber());
        return productoRepository.findAll(pageable)
                .map(productoMapper::toDTO);
    }

    @Override
    public Page<ProductoDTO> findByCategoriaPaginado(String nombreCategoria, Pageable pageable) {
        log.debug("Obteniendo productos de categoría '{}' paginados", nombreCategoria);
        return productoRepository.findByCategoriaNombre(nombreCategoria, pageable)
                .map(productoMapper::toDTO);
    }

    @Override
    public Page<ProductoDTO> buscarPorCategoriaYNombre(String categoria, String nombre, Pageable pageable) {
        log.debug("Buscando productos - Categoría: {}, Nombre: {}", categoria, nombre);
        
        if ((categoria == null || categoria.isEmpty()) && (nombre == null || nombre.isEmpty())) {
            return findAllPaginado(pageable);
        } else if (categoria != null && !categoria.isEmpty() && (nombre == null || nombre.isEmpty())) {
            return findByCategoriaPaginado(categoria, pageable);
        } else if ((categoria == null || categoria.isEmpty()) && nombre != null && !nombre.isEmpty()) {
            return productoRepository.findByNombreContainingIgnoreCase(nombre, pageable)
                    .map(productoMapper::toDTO);
        } else {
            return productoRepository.findByCategoriaNombreAndNombreContainingIgnoreCase(categoria, nombre, pageable)
                    .map(productoMapper::toDTO);
        }
    }

    // ============= CRUD OPERATIONS =============

    @Override
    @Transactional
    public ProductoDTO create(ProductoCreateDTO createDTO, MultipartFile imagen) {
        log.info("Creando nuevo producto: {}", createDTO.getNombre());
        
        // Validar datos
        validateProductoData(createDTO);
        
        // Obtener o crear la categoría
        Categoria categoria = obtenerOCrearCategoria(createDTO);
        
        // Convertir DTO a Entity
        Producto producto = productoMapper.toEntityWithCategoria(createDTO, categoria);
        
        // Subir imagen si existe
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String imageUrl = imgBBUploader.subirImagen(imagen);
                producto.setImagenUrl(imageUrl);
                log.debug("Imagen subida exitosamente: {}", imageUrl);
            } catch (Exception e) {
                log.error("Error al subir imagen", e);
                throw new RuntimeException("Error al subir la imagen: " + e.getMessage());
            }
        } else {
            producto.setImagenUrl("/imagenes/imagenpordefecto.png");
        }
        
        Producto savedProducto = productoRepository.save(producto);
        log.info("Producto creado exitosamente con ID: {}", savedProducto.getIdProducto());
        
        return productoMapper.toDTO(savedProducto);
    }

    @Override
    @Transactional
    public ProductoDTO update(Integer id, ProductoCreateDTO updateDTO, MultipartFile imagen) {
        log.info("Actualizando producto ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        // Validar datos
        validateProductoData(updateDTO);
        
        // Actualizar categoría si cambió
        if (updateDTO.getIdCategoria() != null && 
            !updateDTO.getIdCategoria().equals(producto.getCategoria().getIdCategoria())) {
            Categoria nuevaCategoria = categoriaRepository.findById(updateDTO.getIdCategoria())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            producto.setCategoria(nuevaCategoria);
        }
        
        // Actualizar campos
        productoMapper.updateEntity(producto, updateDTO);
        
        // Actualizar imagen si se proporciona una nueva
        if (imagen != null && !imagen.isEmpty()) {
            try {
                String imageUrl = imgBBUploader.subirImagen(imagen);
                producto.setImagenUrl(imageUrl);
                log.debug("Imagen actualizada: {}", imageUrl);
            } catch (Exception e) {
                log.error("Error al actualizar imagen", e);
                throw new RuntimeException("Error al actualizar la imagen: " + e.getMessage());
            }
        }
        
        Producto updatedProducto = productoRepository.save(producto);
        log.info("Producto actualizado exitosamente: {}", id);
        
        return productoMapper.toDTO(updatedProducto);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        log.info("Desactivando producto ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        producto.setEstado(false);
        productoRepository.save(producto);
        
        log.info("Producto desactivado exitosamente: {}", id);
    }
    
    @Override
    @Transactional
    public void activarProducto(Integer id) {
        log.info("Activando producto ID: {}", id);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        producto.setEstado(true);
        productoRepository.save(producto);
        
        log.info("Producto activado exitosamente: {}", id);
    }
    
    @Override
    @Transactional
    public void cambiarEstado(Integer id, Boolean nuevoEstado) {
        log.info("Cambiando estado del producto ID: {} a {}", id, nuevoEstado);
        
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
        
        producto.setEstado(nuevoEstado);
        productoRepository.save(producto);
        
        log.info("Estado del producto actualizado exitosamente: {}", id);
    }

    @Override
    @Transactional
    public void deletePermanently(Integer id) {
        log.warn("Eliminando permanentemente producto ID: {}", id);
        
        if (!productoRepository.existsById(id)) {
            throw new IllegalArgumentException("Producto no encontrado con ID: " + id);
        }
        
        productoRepository.deleteById(id);
        log.info("Producto eliminado permanentemente: {}", id);
    }

    // ============= GESTIÓN DE STOCK =============

    @Override
    @Transactional
    public void reducirStock(Integer idProducto, int cantidad) {
        log.info("Reduciendo stock del producto ID: {} en {} unidades", idProducto, cantidad);
        
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + idProducto));
        
        producto.reducirStock(cantidad);
        productoRepository.save(producto);
        
        log.info("Stock reducido. Stock actual: {}", producto.getStock());
    }

    @Override
    @Transactional
    public void aumentarStock(Integer idProducto, int cantidad) {
        log.info("Aumentando stock del producto ID: {} en {} unidades", idProducto, cantidad);
        
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + idProducto));
        
        producto.aumentarStock(cantidad);
        productoRepository.save(producto);
        
        log.info("Stock aumentado. Stock actual: {}", producto.getStock());
    }

    @Override
    public boolean tieneStockSuficiente(Integer idProducto, int cantidadRequerida) {
        return productoRepository.findById(idProducto)
                .map(p -> p.getStock() >= cantidadRequerida)
                .orElse(false);
    }

    // ============= VALIDACIONES =============

    @Override
    public boolean isDisponible(Integer idProducto) {
        return productoRepository.findById(idProducto)
                .map(Producto::isDisponible)
                .orElse(false);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return !productoRepository.findByNombreContaining(nombre).isEmpty();
    }

    // ============= MÉTODOS PRIVADOS AUXILIARES =============

    private void validateProductoData(ProductoCreateDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio");
        }
        if (dto.getPrecio() == null || dto.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        if (dto.getStock() == null || dto.getStock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }

    private Categoria obtenerOCrearCategoria(ProductoCreateDTO dto) {
        Categoria categoria;
        
        if (dto.getIdCategoria() != null) {
            // Buscar por ID
            categoria = categoriaRepository.findById(dto.getIdCategoria())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + dto.getIdCategoria()));
        } else if (dto.getNombreCategoria() != null && !dto.getNombreCategoria().trim().isEmpty()) {
            // Buscar por nombre o crear nueva
            categoria = categoriaRepository.findByNombre(dto.getNombreCategoria().trim());
            if (categoria == null) {
                categoria = Categoria.builder()
                        .nombre(dto.getNombreCategoria().trim())
                        .build();
                categoria = categoriaRepository.save(categoria);
                log.info("Nueva categoría creada: {}", categoria.getNombre());
            }
        } else {
            throw new IllegalArgumentException("Debe proporcionar un ID de categoría o un nombre de categoría");
        }
        
        return categoria;
    }
}
