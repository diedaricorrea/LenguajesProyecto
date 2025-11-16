package com.example.Ejemplo.dto;

import com.example.Ejemplo.models.EstadoPedido;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO para visualizar pedidos en el sistema
 * Incluye información del usuario y detalles de productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {
    private Integer idPedido;
    private String codigoPedido;
    private LocalDateTime fechaPedido;
    private LocalTime fechaEntrega;
    private EstadoPedido estado;
    
    // Información del usuario
    private Integer idUsuario;
    private String nombreUsuario;
    
    // Detalles del pedido
    private List<DetallePedidoDTO> detalles;
    private Double total;
    
    // Información de seguimiento
    private LocalDateTime fechaCambioEstado;
    private String estadoAnterior;
    
    /**
     * Obtiene el nombre de display del estado actual
     */
    public String getEstadoDisplayName() {
        return estado != null ? estado.getDisplayName() : "Desconocido";
    }
    
    /**
     * Obtiene la clase Bootstrap para el badge del estado
     */
    public String getEstadoBootstrapClass() {
        return estado != null ? estado.getBootstrapClass() : "secondary";
    }
    
    /**
     * Obtiene el icono Bootstrap Icons para el estado
     */
    public String getEstadoIconClass() {
        return estado != null ? estado.getIconClass() : "bi-question-circle";
    }
    
    /**
     * Verifica si el pedido puede avanzar al siguiente estado
     */
    public boolean puedeAvanzar() {
        return estado != null && !estado.esFinal();
    }
    
    /**
     * Obtiene el siguiente estado posible
     */
    public EstadoPedido getSiguienteEstado() {
        return estado != null ? estado.getSiguienteEstado() : null;
    }
}
