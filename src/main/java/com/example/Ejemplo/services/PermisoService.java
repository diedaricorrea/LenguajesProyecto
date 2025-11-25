package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.PermisoDTO;
import java.util.List;
import java.util.Map;


public interface PermisoService {
    

    List<PermisoDTO> findAllPermisos();
    

    List<PermisoDTO> findPermisosPorModulo(String modulo);
    
 
    Map<String, List<PermisoDTO>> findPermisosAgrupadosPorModulo();
    

    PermisoDTO findPermisoById(Integer id);
    

    List<String> findModulos();
    

    List<PermisoDTO> findPermisosNoAsignadosARol(Integer idRol);
    

    List<PermisoDTO> findPermisosPorRol(Integer idRol);
}
