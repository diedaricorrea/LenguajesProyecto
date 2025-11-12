package com.example.Ejemplo.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "detalle_pedido")
public class DetallePedido {
    @EmbeddedId
    private DetallePedidoId detallepedidoId;

    @ManyToOne
    @MapsId("idPedido")
    @JoinColumn(name = "id_pedido")
    private Pedido pedido;

    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "id_producto")
    private Producto producto;
    @Column(nullable = false)
    @jakarta.validation.constraints.NotNull
    @jakarta.validation.constraints.Min(1)
    private Integer cantidad;

    @Column(nullable = false)
    @jakarta.validation.constraints.NotNull
    private double subtotal;
}
