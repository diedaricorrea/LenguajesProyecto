package com.example.Ejemplo.models;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "carrito")
public class Carrito {

    @EmbeddedId
    private CarritoId carritoid;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto idProducto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false, precision = 6, scale = 3)
    private BigDecimal total;

}