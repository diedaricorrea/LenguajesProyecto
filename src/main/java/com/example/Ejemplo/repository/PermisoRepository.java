package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.Permiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PermisoRepository extends JpaRepository<Permiso, Integer> {
    Optional<Permiso> findByNombre(String nombre);
    List<Permiso> findByModulo(String modulo);
    boolean existsByNombre(String nombre);
}
