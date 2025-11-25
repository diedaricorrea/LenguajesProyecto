package com.example.Ejemplo.services;

import com.example.Ejemplo.dto.PedidoDTO;
import com.example.Ejemplo.models.EstadoPedido;
import com.example.Ejemplo.models.Pedido;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PedidosService {
    Pedido guardarPedido(Pedido pedido);
    int eliminarPedido(String codigoPedido);
    
    List<Pedido> obtenerTodosPedidos();
    List<Pedido> obtenerPedidosPorUsuario(int idUsuario);
    Optional<Pedido> buscarPorId(Integer id);
    List<Pedido> obtenerPedidosPorEstado(EstadoPedido estado);
    List<Pedido> buscarPorCodigoPedido(String codigoPedido);
    
    Map<EstadoPedido, List<PedidoDTO>> agruparPedidosPorEstado();
    Map<EstadoPedido, List<PedidoDTO>> agruparPedidosPorEstadoYFecha(LocalDate fechaInicio, LocalDate fechaFin);
    List<PedidoDTO> convertirPedidosADTO(List<Pedido> pedidos);

    boolean avanzarEstadoPedido(Integer idPedido, Integer idUsuarioNotificar);
    boolean cancelarPedido(Integer idPedido, String motivo, Integer idUsuarioNotificar);

    String generarCodigoUnico();
    List<String> obtenerTodosLosCodigos();
}
