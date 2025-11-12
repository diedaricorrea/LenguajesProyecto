package com.example.Ejemplo.repository;

import com.example.Ejemplo.models.DetalleVenta;
import com.example.Ejemplo.models.DetalleVentaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.Ejemplo.dto.ProductoMasVendidoDTO;

import java.util.List;

public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, DetalleVentaId> {
    @Query("SELECT ProductoMasVendidoDTO(p.idProducto, p.nombre, p.precio, p.descripcion, SUM(d.cantidad)) " +
            "FROM DetalleVenta d JOIN d.producto p GROUP BY p.idProducto, p.nombre, p.precio, p.descripcion ORDER BY SUM(d.cantidad) DESC")
    List<?> findProductosMasVendidos();
}
