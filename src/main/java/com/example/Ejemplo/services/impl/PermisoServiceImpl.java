package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.dto.PermisoDTO;
import com.example.Ejemplo.mapper.PermisoMapper;
import com.example.Ejemplo.repository.PermisoRepository;
import com.example.Ejemplo.services.PermisoService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermisoServiceImpl implements PermisoService {
    
    private static final Logger logger = LoggerFactory.getLogger(PermisoServiceImpl.class);
    
    private final PermisoRepository permisoRepository;
    private final PermisoMapper permisoMapper;

    @Override
    public List<PermisoDTO> findAllPermisos() {
        logger.info("Obteniendo todos los permisos");
        return permisoRepository.findAll().stream()
                .map(permisoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermisoDTO> findPermisosPorModulo(String modulo) {
        logger.info("Obteniendo permisos del módulo: {}", modulo);
        return permisoRepository.findByModulo(modulo).stream()
                .map(permisoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<PermisoDTO>> findPermisosAgrupadosPorModulo() {
        logger.info("Obteniendo permisos agrupados por módulo");
        
        List<PermisoDTO> todosPermisos = findAllPermisos();
        
        return todosPermisos.stream()
                .collect(Collectors.groupingBy(
                    p -> p.getModulo() != null ? p.getModulo() : "OTROS",
                    TreeMap::new,
                    Collectors.toList()
                ));
    }

    @Override
    public PermisoDTO findPermisoById(Integer id) {
        logger.info("Buscando permiso con ID: {}", id);
        return permisoRepository.findById(id)
                .map(permisoMapper::toDTO)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado con ID: " + id));
    }

    @Override
    public List<String> findModulos() {
        logger.info("Obteniendo módulos distintos");
        return permisoRepository.findDistinctModulos();
    }

    @Override
    public List<PermisoDTO> findPermisosNoAsignadosARol(Integer idRol) {
        logger.info("Obteniendo permisos no asignados al rol ID: {}", idRol);
        return permisoRepository.findPermisosNoAsignadosARol(idRol).stream()
                .map(permisoMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PermisoDTO> findPermisosPorRol(Integer idRol) {
        logger.info("Obteniendo permisos del rol ID: {}", idRol);
        return permisoRepository.findPermisosPorRol(idRol).stream()
                .map(permisoMapper::toDTO)
                .collect(Collectors.toList());
    }
}
