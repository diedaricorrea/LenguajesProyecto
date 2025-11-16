package com.example.Ejemplo.mapper;

import com.example.Ejemplo.dto.DetallePedidoDTO;
import com.example.Ejemplo.dto.PedidoDTO;
import com.example.Ejemplo.models.DetallePedido;
import com.example.Ejemplo.models.Pedido;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper para convertir entre entidades Pedido y DTOs
 * Separa la lógica de presentación de los modelos JPA
 */
@Component
public class PedidoMapper {
    
    /**
     * Convierte una entidad Pedido a PedidoDTO completo
     */
    public PedidoDTO toDTO(Pedido pedido) {
        if (pedido == null) {
            return null;
        }
        
        List<DetallePedidoDTO> detalles = pedido.getDetallePedido() != null
                ? pedido.getDetallePedido().stream()
                    .map(this::toDetallePedidoDTO)
                    .collect(Collectors.toList())
                : List.of();
        
        Double total = detalles.stream()
                .mapToDouble(DetallePedidoDTO::getSubtotal)
                .sum();
        
        return PedidoDTO.builder()
                .idPedido(pedido.getIdPedido())
                .codigoPedido(pedido.getCodigoPedido())
                .fechaPedido(pedido.getFechaPedido())
                .fechaEntrega(pedido.getFechaEntrega())
                .estado(pedido.getEstado())
                .idUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getIdUsuario() : null)
                .nombreUsuario(pedido.getUsuario() != null ? pedido.getUsuario().getNombre() : "Desconocido")
                .detalles(detalles)
                .total(total)
                .build();
    }
    
    /**
     * Convierte una entidad DetallePedido a DetallePedidoDTO
     */
    public DetallePedidoDTO toDetallePedidoDTO(DetallePedido detalle) {
        if (detalle == null || detalle.getProducto() == null) {
            return null;
        }
        
        String nombreCategoria = detalle.getProducto().getCategoria() != null 
                ? detalle.getProducto().getCategoria().getNombre() 
                : "Sin categoría";
        
        return DetallePedidoDTO.builder()
                .idProducto(detalle.getProducto().getIdProducto())
                .nombreProducto(detalle.getProducto().getNombre())
                .nombreCategoria(nombreCategoria)
                .precioUnitario(detalle.getProducto().getPrecio())
                .cantidad(detalle.getCantidad())
                .subtotal(detalle.getSubtotal())
                .imagenUrl(detalle.getProducto().getImagenUrl())
                .build();
    }
    
    /**
     * Convierte una lista de Pedidos a DTOs
     */
    public List<PedidoDTO> toDTOList(List<Pedido> pedidos) {
        if (pedidos == null) {
            return List.of();
        }
        
        return pedidos.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
