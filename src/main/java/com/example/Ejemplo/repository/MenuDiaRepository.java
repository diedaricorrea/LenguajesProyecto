package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.MenuDia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MenuDiaRepository extends JpaRepository<MenuDia, Integer> {
    
    /**
     * Busca menús programados por rango de fechas
     */
    @Query("SELECT m FROM MenuDia m WHERE m.fecha BETWEEN :fechaInicio AND :fechaFin ORDER BY m.fecha ASC")
    List<MenuDia> findByFechaBetween(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Busca menús programados a partir de una fecha
     */
    @Query("SELECT m FROM MenuDia m WHERE m.fecha >= :fecha ORDER BY m.fecha ASC")
    List<MenuDia> findByFechaAfter(@Param("fecha") LocalDateTime fecha);
    
    /**
     * Busca todos los menús ordenados por fecha
     */
    @Query("SELECT m FROM MenuDia m ORDER BY m.fecha DESC")
    List<MenuDia> findAllOrderByFechaDesc();
}
