package com.example.Ejemplo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para los detalles individuales de un pedido
 * Representa cada producto en el pedido con su cantidad y subtotal
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetallePedidoDTO {
    private Integer idProducto;
    private String nombreProducto;
    private String nombreCategoria;
    private Double precioUnitario;
    private Integer cantidad;
    private Double subtotal;
    private String imagenUrl;
    
    /**
     * Calcula el subtotal si no está establecido
     */
    public Double calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            return precioUnitario * cantidad;
        }
        return subtotal;
    }
}
