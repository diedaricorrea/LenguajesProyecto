package com.example.Ejemplo.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.Ejemplo.models.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {

    Categoria findByNombre(String nombre);
    
    /**
     * Cuenta la cantidad de productos asociados a una categoría
     * Optimizado: No carga la colección de productos
     */
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoria.idCategoria = :idCategoria")
    long countProductosByCategoria(@Param("idCategoria") Integer idCategoria);
    
    /**
     * Encuentra una categoría con sus productos inicializados (si es necesario)
     */
    @Query("SELECT c FROM Categoria c LEFT JOIN FETCH c.productos WHERE c.idCategoria = :id")
    Categoria findByIdWithProductos(@Param("id") Integer id);
}