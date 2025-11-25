package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.*;
import java.util.List;
import java.util.Map;

public interface RolEntityService {
    
    List<RolDTO> findAllRoles();
    
    List<RolDTO> findRolesActivos();
    
    RolResponseDTO findRolById(Integer id);
    
    RolResponseDTO findRolByNombre(String nombre);
    
    RolResponseDTO crearRol(RolCreateDTO rolDTO);
    
    RolResponseDTO actualizarRol(Integer id, RolCreateDTO rolDTO);
    
    void cambiarEstado(Integer idRol, boolean nuevoEstado);
    
    void eliminarRol(Integer idRol);
    
    RolResponseDTO asignarPermisos(AsignarPermisosDTO asignarDTO);
    
    void removerPermiso(Integer idRol, Integer idPermiso);

    List<RolResponseDTO> findAllRolesConPermisos();
    
    boolean existeRolPorNombre(String nombre);

    boolean tieneUsuariosAsignados(Integer idRol);
    
    Map<String, Object> obtenerEstadisticas();
}
