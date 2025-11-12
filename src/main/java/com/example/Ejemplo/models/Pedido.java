package com.example.Ejemplo.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPedido;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "codigo_pedido")
    private String codigoPedido;

    @Column(name = "fecha_pedido")
    private LocalDateTime fechaPedido;
    @Column(name = "fecha_entrega")
    private LocalTime fechaEntrega;
    @Column(name = "estado")
    private boolean estado;
    
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<DetallePedido> detallePedido;

    @PrePersist
    protected void onCreate() {
        fechaPedido = LocalDateTime.now();
    }


}
