package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    Optional<Permiso> findByNombre(String nombre);
    List<Permiso> findByModulo(String modulo);
    List<Permiso> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByNombre(String nombre);
    
    @Query("SELECT DISTINCT p.modulo FROM Permiso p ORDER BY p.modulo")
    List<String> findDistinctModulos();
    
    @Query("SELECT p FROM Permiso p WHERE p NOT IN " +
           "(SELECT perm FROM RolEntity r JOIN r.permisos perm WHERE r.idRol = :idRol)")
    List<Permiso> findPermisosNoAsignadosARol(@Param("idRol") Integer idRol);
    
    @Query("SELECT perm FROM RolEntity r JOIN r.permisos perm WHERE r.idRol = :idRol")
    List<Permiso> findPermisosPorRol(@Param("idRol") Integer idRol);
}
