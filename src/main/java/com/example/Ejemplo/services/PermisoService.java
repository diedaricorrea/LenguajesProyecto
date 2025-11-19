package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.PermisoDTO;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestión de permisos
 */
public interface PermisoService {
    
    /**
     * Obtiene todos los permisos
     */
    List<PermisoDTO> findAllPermisos();
    
    /**
     * Obtiene permisos por módulo
     */
    List<PermisoDTO> findPermisosPorModulo(String modulo);
    
    /**
     * Obtiene permisos agrupados por módulo
     */
    Map<String, List<PermisoDTO>> findPermisosAgrupadosPorModulo();
    
    /**
     * Obtiene un permiso por ID
     */
    PermisoDTO findPermisoById(Integer id);
    
    /**
     * Obtiene todos los módulos distintos
     */
    List<String> findModulos();
    
    /**
     * Obtiene permisos NO asignados a un rol
     */
    List<PermisoDTO> findPermisosNoAsignadosARol(Integer idRol);
    
    /**
     * Obtiene permisos asignados a un rol
     */
    List<PermisoDTO> findPermisosPorRol(Integer idRol);
}
