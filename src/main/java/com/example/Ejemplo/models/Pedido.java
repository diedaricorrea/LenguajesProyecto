package com.example.Ejemplo.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
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
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoPedido estado = EstadoPedido.PENDIENTE;
    
    @Column(name = "notas_especiales")
    private String notasEspeciales;
    
    @Column(name = "metodo_pago")
    private String metodoPago;
    
    @OneToMany(mappedBy = "pedido", fetch = FetchType.LAZY)
    private List<DetallePedido> detallePedido;

    @PrePersist
    protected void onCreate() {
        fechaPedido = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoPedido.PENDIENTE;
        }
    }
    
    /**
     * Cambia el estado del pedido si la transición es válida
     * @param nuevoEstado Estado al que se quiere transicionar
     * @return true si la transición fue exitosa, false si no es válida
     */
    public boolean cambiarEstado(EstadoPedido nuevoEstado) {
        if (estado.puedeTransicionarA(nuevoEstado)) {
            this.estado = nuevoEstado;
            return true;
        }
        return false;
    }
    
    /**
     * Avanza al siguiente estado en el flujo normal
     */
    public boolean avanzarEstado() {
        EstadoPedido siguiente = estado.getSiguienteEstado();
        if (siguiente != estado) {
            this.estado = siguiente;
            return true;
        }
        return false;
    }
    
    /**
     * Cancela el pedido si es posible
     */
    public boolean cancelar() {
        return cambiarEstado(EstadoPedido.CANCELADO);
    }


}
