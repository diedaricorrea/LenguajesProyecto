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
    

    List<ProductoDTO> obtenerTodos();
    
    List<ProductoResponseDTO> obtenerTodosConDetalles();
    
    Optional<ProductoDTO> buscarPorId(Integer id);
 
    Optional<ProductoResponseDTO> buscarPorIdConDetalles(Integer id);
    
 
    Optional<ProductoDTO> buscarPorNombre(String nombre);
    

    List<ProductoDTO> obtenerRecientes(int limite);
    
   
    List<ProductoDTO> buscarPorNombreContiene(String nombre);
    

    Page<ProductoDTO> obtenerTodosPaginado(Pageable pageable);

    Page<ProductoDTO> obtenerPorCategoriaPaginado(String nombreCategoria, Pageable pageable);
    

 
    Page<ProductoDTO> buscarPorCategoriaYNombre(String categoria, String nombre, Pageable pageable);
    

    ProductoDTO crear(ProductoCreateDTO createDTO, MultipartFile imagen);
    

    ProductoDTO actualizar(Integer id, ProductoCreateDTO updateDTO, MultipartFile imagen);
    

    void eliminarPorId(Integer id);
    
  
    void activarProducto(Integer id);
    
    void cambiarEstado(Integer id, Boolean nuevoEstado);
    

    void eliminarPermanentemente(Integer id);
    
    void reducirStock(Integer idProducto, int cantidad);
    
    void aumentarStock(Integer idProducto, int cantidad);
    
    boolean tieneStockSuficiente(Integer idProducto, int cantidadRequerida);
    
    boolean estaDisponible(Integer idProducto);
    
    boolean existePorNombre(String nombre);
}
