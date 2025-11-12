package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.Producto;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findProductoByCategoria_Nombre(String categoriaNombre);

    List<Producto> findByNombreContaining(String nombre);
    List<Producto> findByEstadoTrue();    @Override
    @Query("SELECT p FROM Producto p WHERE p.stock > 0")
    Page<Producto> findAll(Pageable pageable);
    @Query("SELECT p FROM Producto p WHERE p.stock > 0 AND p.categoria.nombre = :nombre")
    Page<Producto> findByCategoriaNombre(@Param("nombre") String nombre, Pageable pageable);
    @Query("SELECT p FROM Producto p WHERE p.stock > 0 AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Producto> findByNombreContainingIgnoreCase(@Param("nombre") String nombre, Pageable pageable);
    @Query(" SELECT p FROM Producto p WHERE p.stock > 0 AND p.categoria.nombre = :categoria AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    Page<Producto> findByCategoriaNombreAndNombreContainingIgnoreCase(String categoria, String nombre, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE LOWER(p.nombre) LIKE concat('%',:nombre,'%')")
    List<Producto> findByNombreContainingIgnoreCase(@Param("nombre") String nombre);

    @Modifying
    @Transactional
    @Query("UPDATE Producto p set p.stock = :stock WHERE p.idProducto = :idProducto")
    int reducirStock(int stock, int idProducto);
}
