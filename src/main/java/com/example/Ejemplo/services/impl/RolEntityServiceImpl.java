package com.example.Ejemplo.services.impl;

import com.example.Ejemplo.dto.*;
import com.example.Ejemplo.mapper.RolMapper;
import com.example.Ejemplo.models.Permiso;
import com.example.Ejemplo.models.RolEntity;
import com.example.Ejemplo.repository.PermisoRepository;
import com.example.Ejemplo.repository.RolEntityRepository;
import com.example.Ejemplo.services.RolEntityService;
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
public class RolEntityServiceImpl implements RolEntityService {
    
    private static final Logger logger = LoggerFactory.getLogger(RolEntityServiceImpl.class);
    private static final Set<String> ROLES_SISTEMA = Set.of("ADMINISTRADOR", "TRABAJADOR", "USUARIO");
    
    private final RolEntityRepository rolRepository;
    private final PermisoRepository permisoRepository;
    private final RolMapper rolMapper;

    @Override
    public List<RolDTO> findAllRoles() {
        logger.info("Obteniendo todos los roles");
        return rolRepository.findAll().stream()
                .map(rolMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<RolDTO> findRolesActivos() {
        logger.info("Obteniendo roles activos");
        return rolRepository.findByActivo(true).stream()
                .map(rolMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RolResponseDTO findRolById(Integer id) {
        logger.info("Buscando rol con ID: {}", id);
        
        RolEntity rol = rolRepository.findByIdWithPermisos(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
        
        return rolMapper.toResponseDTO(rol);
    }

    @Override
    public RolResponseDTO findRolByNombre(String nombre) {
        logger.info("Buscando rol por nombre: {}", nombre);
        
        RolEntity rol = rolRepository.findByNombreWithPermisos(nombre)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado: " + nombre));
        
        return rolMapper.toResponseDTO(rol);
    }

    @Override
    @Transactional
    public RolResponseDTO crearRol(RolCreateDTO rolDTO) {
        logger.info("Creando nuevo rol: {}", rolDTO.getNombre());
        
        // Validar que no exista
        String nombreNormalizado = rolDTO.getNombre().toUpperCase().trim();
        if (rolRepository.existsByNombre(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + nombreNormalizado);
        }
        
        // Crear rol
        RolEntity rol = rolMapper.toEntity(rolDTO);
        RolEntity guardado = rolRepository.save(rol);
        
        logger.info("Rol creado exitosamente con ID: {}", guardado.getIdRol());
        return rolMapper.toResponseDTO(guardado);
    }

    @Override
    @Transactional
    public RolResponseDTO actualizarRol(Integer id, RolCreateDTO rolDTO) {
        logger.info("Actualizando rol ID: {}", id);
        
        RolEntity rol = rolRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + id));
        
        // Verificar si es rol del sistema
        if (ROLES_SISTEMA.contains(rol.getNombre())) {
            throw new IllegalArgumentException("No se puede modificar un rol del sistema");
        }
        
        // Validar nombre único
        String nombreNuevo = rolDTO.getNombre().toUpperCase().trim();
        if (!rol.getNombre().equals(nombreNuevo) && 
            rolRepository.existsByNombre(nombreNuevo)) {
            throw new IllegalArgumentException("Ya existe un rol con el nombre: " + nombreNuevo);
        }
        
        rolMapper.updateEntity(rol, rolDTO);
        RolEntity actualizado = rolRepository.save(rol);
        
        logger.info("Rol actualizado exitosamente: {}", actualizado.getIdRol());
        return rolMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void cambiarEstado(Integer idRol, boolean nuevoEstado) {
        logger.info("Cambiando estado del rol {} a {}", idRol, nuevoEstado);
        
        RolEntity rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + idRol));
        
        if (ROLES_SISTEMA.contains(rol.getNombre())) {
            throw new IllegalArgumentException("No se puede desactivar un rol del sistema");
        }
        
        rol.setActivo(nuevoEstado);
        rolRepository.save(rol);
    }

    @Override
    @Transactional
    public void eliminarRol(Integer idRol) {
        logger.info("Eliminando rol ID: {}", idRol);
        
        RolEntity rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado con ID: " + idRol));
        
        // Verificar si es rol del sistema
        if (ROLES_SISTEMA.contains(rol.getNombre())) {
            throw new IllegalArgumentException("No se puede eliminar un rol del sistema");
        }
        
        // Verificar si tiene usuarios
        if (tieneUsuariosAsignados(idRol)) {
            throw new IllegalArgumentException(
                "No se puede eliminar el rol porque tiene usuarios asignados. " +
                "Desactívelo o reasigne los usuarios primero.");
        }
        
        rolRepository.delete(rol);
        logger.info("Rol eliminado exitosamente");
    }

    @Override
    @Transactional
    public RolResponseDTO asignarPermisos(AsignarPermisosDTO asignarDTO) {
        logger.info("Asignando permisos al rol ID: {}", asignarDTO.getIdRol());
        
        RolEntity rol = rolRepository.findByIdWithPermisos(asignarDTO.getIdRol())
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        
        List<Permiso> permisos = permisoRepository.findAllById(asignarDTO.getIdsPermisos());
        
        if (permisos.size() != asignarDTO.getIdsPermisos().size()) {
            throw new IllegalArgumentException("Algunos permisos no fueron encontrados");
        }
        
        rol.getPermisos().clear();

        permisos.forEach(rol::agregarPermiso);
        
        RolEntity actualizado = rolRepository.save(rol);
        logger.info("Permisos asignados exitosamente. Total: {}", permisos.size());
        
        return rolMapper.toResponseDTO(actualizado);
    }

    @Override
    @Transactional
    public void removerPermiso(Integer idRol, Integer idPermiso) {
        logger.info("Removiendo permiso {} del rol {}", idPermiso, idRol);
        
        RolEntity rol = rolRepository.findByIdWithPermisos(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        
        Permiso permiso = permisoRepository.findById(idPermiso)
                .orElseThrow(() -> new IllegalArgumentException("Permiso no encontrado"));
        
        rol.removerPermiso(permiso);
        rolRepository.save(rol);
    }

    @Override
    public List<RolResponseDTO> findAllRolesConPermisos() {
        logger.info("Obteniendo todos los roles con permisos");
        return rolRepository.findAllActivosConPermisos().stream()
                .map(rolMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existeRolPorNombre(String nombre) {
        return rolRepository.existsByNombre(nombre.toUpperCase().trim());
    }

    @Override
    public boolean tieneUsuariosAsignados(Integer idRol) {
        RolEntity rol = rolRepository.findById(idRol)
                .orElseThrow(() -> new IllegalArgumentException("Rol no encontrado"));
        
        return rol.getUsuarios() != null && !rol.getUsuarios().isEmpty();
    }

    @Override
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRoles", rolRepository.count());
        stats.put("rolesActivos", rolRepository.countByActivo(true));
        stats.put("rolesInactivos", rolRepository.countByActivo(false));
        stats.put("rolesSistema", ROLES_SISTEMA.size());
        
        return stats;
    }
}
