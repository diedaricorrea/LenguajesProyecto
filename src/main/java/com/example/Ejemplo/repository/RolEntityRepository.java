package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.RolEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface RolEntityRepository extends JpaRepository<RolEntity, Integer> {
    Optional<RolEntity> findByNombre(String nombre);
    List<RolEntity> findByActivoTrue();
    boolean existsByNombre(String nombre);
    
    @Query("SELECT r FROM RolEntity r LEFT JOIN FETCH r.permisos WHERE r.nombre = :nombre")
    Optional<RolEntity> findByNombreWithPermisos(String nombre);
}
