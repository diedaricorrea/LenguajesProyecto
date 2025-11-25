package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.CategoriaCreateDTO;
import com.example.Ejemplo.dto.CategoriaDTO;
import com.example.Ejemplo.dto.CategoriaResponseDTO;
import java.util.List;
import java.util.Optional;


public interface CategoriaService {
    
    List<CategoriaDTO> obtenerTodas();
    
    List<CategoriaResponseDTO> obtenerTodasConDetalles();
    
    Optional<CategoriaDTO> buscarPorId(Integer id);
    
    Optional<CategoriaResponseDTO> buscarPorIdConDetalles(Integer id);
    
    Optional<CategoriaDTO> buscarPorNombre(String nombre);
    
    CategoriaDTO crear(CategoriaCreateDTO createDTO);
    
    CategoriaDTO actualizar(Integer id, CategoriaCreateDTO updateDTO);

    void eliminarPorId(Integer id);
    
    boolean existePorNombre(String nombre);
    
    long contarProductosPorCategoria(Integer idCategoria);
    
    boolean puedeSerEliminada(Integer idCategoria);
}
