package com.example.Ejemplo.dto;

import java.math.BigDecimal;

public class ProductoMasVendidoDTO {
    private final Integer id;
    private final String nombre;
    private final BigDecimal precio;
    private final String descripcion;
    private final Integer totalVendidos;

    public ProductoMasVendidoDTO(Integer id, String nombre, BigDecimal precio, String descripcion, Long totalVendidos) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.totalVendidos = totalVendidos != null ? totalVendidos.intValue() : 0;
    }

    public Integer getId() { return id; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public String getDescripcion() { return descripcion; }
    public Integer getTotalVendidos() { return totalVendidos; }
}
