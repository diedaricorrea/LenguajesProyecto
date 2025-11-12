package com.example.Ejemplo.services;

import com.example.Ejemplo.models.DetallePedido;

import java.util.List;

public interface DetallePedidoService {
    int saveDetallePedido(DetallePedido detallePedido);

    List<DetallePedido> findAllDetallePedido();
}
