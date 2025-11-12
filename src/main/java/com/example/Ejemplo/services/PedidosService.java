package com.example.Ejemplo.services;

import com.example.Ejemplo.models.Pedido;

import java.util.List;
import java.util.Optional;

public interface PedidosService {
    Pedido guardarPedido(Pedido pedido);

    List<Pedido> findAllPedidos();
    
    List<Pedido> findByUsuario_Id(int id);

    int deletePedido(String codigoPedido);

    String generarCodigoUnico();

    List<String> obtenerTodosLosCodigos();
}
