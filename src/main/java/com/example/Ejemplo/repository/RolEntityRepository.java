package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RolEntityRepository extends JpaRepository<RolEntity, Integer> {
    Optional<RolEntity> findByNombre(String nombre);
    List<RolEntity> findByActivoTrue();
    List<RolEntity> findByActivo(boolean activo);
    List<RolEntity> findByNombreContainingIgnoreCase(String nombre);
    boolean existsByNombre(String nombre);
    
    @Query("SELECT r FROM RolEntity r LEFT JOIN FETCH r.permisos WHERE r.nombre = :nombre")
    Optional<RolEntity> findByNombreWithPermisos(@Param("nombre") String nombre);
    
    @Query("SELECT DISTINCT r FROM RolEntity r LEFT JOIN FETCH r.permisos WHERE r.activo = true")
    List<RolEntity> findAllActivosConPermisos();
    
    @Query("SELECT r FROM RolEntity r LEFT JOIN FETCH r.permisos WHERE r.idRol = :idRol")
    Optional<RolEntity> findByIdWithPermisos(@Param("idRol") Integer idRol);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM RolEntity r " +
           "WHERE r.nombre = :nombre AND r.idRol <> :idRol")
    boolean existsByNombreAndIdRolNot(@Param("nombre") String nombre, @Param("idRol") Integer idRol);
    
    long countByActivo(boolean activo);
}
