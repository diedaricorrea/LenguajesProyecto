package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.*;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestión de roles
 */
public interface RolEntityService {
    
    /**
     * Obtiene todos los roles
     */
    List<RolDTO> findAllRoles();
    
    /**
     * Obtiene roles activos
     */
    List<RolDTO> findRolesActivos();
    
    /**
     * Obtiene un rol por ID
     */
    RolResponseDTO findRolById(Integer id);
    
    /**
     * Busca un rol por nombre
     */
    RolResponseDTO findRolByNombre(String nombre);
    
    /**
     * Crea un nuevo rol
     */
    RolResponseDTO crearRol(RolCreateDTO rolDTO);
    
    /**
     * Actualiza un rol existente
     */
    RolResponseDTO actualizarRol(Integer id, RolCreateDTO rolDTO);
    
    /**
     * Cambia el estado de un rol
     */
    void cambiarEstado(Integer idRol, boolean nuevoEstado);
    
    /**
     * Elimina un rol (si no tiene usuarios asignados)
     */
    void eliminarRol(Integer idRol);
    
    /**
     * Asigna permisos a un rol
     */
    RolResponseDTO asignarPermisos(AsignarPermisosDTO asignarDTO);
    
    /**
     * Remueve un permiso de un rol
     */
    void removerPermiso(Integer idRol, Integer idPermiso);
    
    /**
     * Obtiene roles con sus permisos cargados
     */
    List<RolResponseDTO> findAllRolesConPermisos();
    
    /**
     * Verifica si un rol existe por nombre
     */
    boolean existeRolPorNombre(String nombre);
    
    /**
     * Verifica si un rol tiene usuarios asignados
     */
    boolean tieneUsuariosAsignados(Integer idRol);
    
    /**
     * Obtiene estadísticas de roles
     */
    Map<String, Object> obtenerEstadisticas();
}
