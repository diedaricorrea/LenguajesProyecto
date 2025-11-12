package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.dto.CategoriaCreateDTO;
import com.example.Ejemplo.dto.CategoriaDTO;
import com.example.Ejemplo.dto.CategoriaMapper;
import com.example.Ejemplo.dto.CategoriaResponseDTO;
import com.example.Ejemplo.models.Categoria;
import com.example.Ejemplo.repository.CategoriaRepository;
import com.example.Ejemplo.services.CategoriaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de Categorías usando DTOs
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    public List<CategoriaDTO> findAll() {
        log.debug("Obteniendo todas las categorías como DTOs");
        return categoriaRepository.findAll().stream()
                .map(categoriaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaResponseDTO> findAllWithDetails() {
        log.debug("Obteniendo todas las categorías con detalles");
        List<Categoria> categorias = categoriaRepository.findAll();
        
        return categorias.stream()
                .map(categoria -> {
                    // Usar consulta optimizada para contar productos
                    long cantidadProductos = categoriaRepository.countProductosByCategoria(categoria.getIdCategoria());
                    
                    return CategoriaResponseDTO.builder()
                            .idCategoria(categoria.getIdCategoria())
                            .nombre(categoria.getNombre())
                            .cantidadProductos(cantidadProductos)
                            .tieneProdutos(cantidadProductos > 0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoriaDTO> findById(Integer id) {
        log.debug("Buscando categoría por ID: {}", id);
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toDTO);
    }

    @Override
    public Optional<CategoriaResponseDTO> findByIdWithDetails(Integer id) {
        log.debug("Buscando categoría con detalles por ID: {}", id);
        return categoriaRepository.findById(id)
                .map(categoriaMapper::toResponseDTO);
    }

    @Override
    public Optional<CategoriaDTO> findByNombre(String nombre) {
        log.debug("Buscando categoría por nombre: {}", nombre);
        Categoria categoria = categoriaRepository.findByNombre(nombre);
        return Optional.ofNullable(categoria)
                .map(categoriaMapper::toDTO);
    }

    @Override
    @Transactional
    public CategoriaDTO create(CategoriaCreateDTO createDTO) {
        log.info("Creando nueva categoría: {}", createDTO.getNombre());
        
        // Validar que el nombre no esté vacío
        if (createDTO.getNombre() == null || createDTO.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la categoría no puede estar vacío");
        }
        
        // Validar que no exista otra categoría con el mismo nombre
        if (existsByNombre(createDTO.getNombre().trim())) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + createDTO.getNombre());
        }
        
        Categoria categoria = categoriaMapper.toEntity(createDTO);
        Categoria savedCategoria = categoriaRepository.save(categoria);
        
        log.info("Categoría creada exitosamente con ID: {}", savedCategoria.getIdCategoria());
        return categoriaMapper.toDTO(savedCategoria);
    }

    @Override
    @Transactional
    public CategoriaDTO update(Integer id, CategoriaCreateDTO updateDTO) {
        log.info("Actualizando categoría ID: {}", id);
        
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + id));
        
        // Validar que no exista otra categoría con el mismo nombre (excepto la actual)
        Categoria existente = categoriaRepository.findByNombre(updateDTO.getNombre().trim());
        if (existente != null && !existente.getIdCategoria().equals(id)) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + updateDTO.getNombre());
        }
        
        categoriaMapper.updateEntity(categoria, updateDTO);
        Categoria updatedCategoria = categoriaRepository.save(categoria);
        
        log.info("Categoría actualizada exitosamente: {}", id);
        return categoriaMapper.toDTO(updatedCategoria);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        log.info("Intentando eliminar categoría ID: {}", id);
        
        // Verificar que la categoría existe
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoría no encontrada con ID: " + id);
        }
        
        // Verificar si la categoría tiene productos asociados
        long cantidadProductos = countProductosByCategoria(id);
        if (cantidadProductos > 0) {
            throw new IllegalStateException(
                "No se puede eliminar la categoría porque tiene " + cantidadProductos + " producto(s) asociado(s)"
            );
        }
        
        categoriaRepository.deleteById(id);
        log.info("Categoría eliminada exitosamente: {}", id);
    }

    @Override
    public boolean existsByNombre(String nombre) {
        return categoriaRepository.findByNombre(nombre) != null;
    }

    @Override
    public long countProductosByCategoria(Integer idCategoria) {
        // Usar el método optimizado del repositorio que no carga la colección
        return categoriaRepository.countProductosByCategoria(idCategoria);
    }

    @Override
    public boolean canBeDeleted(Integer idCategoria) {
        return countProductosByCategoria(idCategoria) == 0;
    }
}
