package com.example.Ejemplo.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * Entidad Producto - Representa los productos/platos del sistema
 * Relación: Muchos productos pertenecen a una categoría (N:1)
 */
@Entity
@Table(name = "productos", indexes = {
    @Index(name = "idx_producto_categoria", columnList = "id_categoria"),
    @Index(name = "idx_producto_nombre", columnList = "nombre")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(nullable = false)
    private Integer stock;

    @Column(nullable = false)
    @Builder.Default
    private Boolean estado = true;

    @Column(name = "imagen_url", length = 255)
    private String imagenUrl;

    /**
     * Relación Many-to-One con Categoria
     * EAGER porque casi siempre necesitamos la categoría al cargar un producto
     * Para evitar N+1, usar @EntityGraph en queries específicas si es necesario
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_categoria", nullable = false, foreignKey = @ForeignKey(name = "fk_producto_categoria"))
    private Categoria categoria;
    
    /**
     * Verifica si el producto está disponible para venta
     */
    public boolean isDisponible() {
        return Boolean.TRUE.equals(this.estado) && this.stock > 0;
    }
    
    /**
     * Reduce el stock del producto
     */
    public void reducirStock(int cantidad) {
        if (this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new IllegalStateException("Stock insuficiente para el producto: " + this.nombre);
        }
    }
    
    /**
     * Incrementa el stock del producto
     */
    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
    }
}